package com.libumu.mubook.mt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResultMap {

    List<String> keys;
    List<Long> values;
    Lock mutex;

    public ResultMap () {
        keys = new ArrayList<>();
        values = new ArrayList<>();
        mutex = new ReentrantLock();
    }
    public void put(String key,Long value) throws InterruptedException {
        mutex.lock();
        keys.add(key);
        values.add(value);
        mutex.unlock();
    }

    public List<String> getKeys() {
        return keys;
    }
    public List<Long> getValues() {
        return values;
    }
}

