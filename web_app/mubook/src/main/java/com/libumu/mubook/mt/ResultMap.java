package com.libumu.mubook.mt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResultMap {
    Map<String, Long> results = new HashMap<String, Long>();
    Lock mutex;

    public ResultMap () {
        mutex = new ReentrantLock();
        results = new HashMap<String, Long>();
    }
    public void put(String key,Long value) throws InterruptedException {
        mutex.lock();
        results.put(key, value);
        mutex.unlock();
    }

    public List<String> getKeys() {
        List<String> list= new ArrayList<>(results.keySet());
        return list;
    }
    public List<Long> getValues() {
        List<Long> list= new ArrayList<>(results.values());
        return list;
    }
    public Map<String, Long> getMap(){
        return results;
    }
}

