package com.libumu.mubook.mt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {

    int capacity;
    List<Integer> buffer;
    Lock mutex;
    Condition isFull, isEmpty;

    public Buffer (int capacity) {
        buffer = new ArrayList<>();
        this.capacity = capacity;
        mutex = new ReentrantLock();
        isFull = mutex.newCondition();
        isEmpty = mutex.newCondition();
    }
    public void put(Integer value) throws InterruptedException {
        mutex.lock();
        if ( buffer.size() == capacity) {
            isFull.await();
        }
        buffer.add(value);
        isEmpty.signal();
        mutex.unlock();
    }
    public Integer get() throws InterruptedException {
        Integer value = null;
        mutex.lock();
        if (buffer.size() == 0) {
            isEmpty.await();
        }
        value = buffer.remove(0);
        isFull.signal();
        mutex.unlock();
        return value;
    }
    public List<Integer> get2Values() throws InterruptedException {
        List<Integer> value = new ArrayList<>();
        mutex.lock();
        if (buffer.size() == 0) {
            isEmpty.await();
        }
        value.add(buffer.remove(0));
        value.add(buffer.remove(0));
        isFull.signal();
        mutex.unlock();
        return value;
    }
    public boolean empty() {

        return (buffer.size() == 0);
    }

    public List<Integer> getBuffer() {
        return buffer;
    }
}

