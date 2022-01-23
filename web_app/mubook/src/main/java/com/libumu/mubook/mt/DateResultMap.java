package com.libumu.mubook.mt;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DateResultMap {
    Map<String, Long> results = new HashMap<String, Long>();
    Lock mutex;

    public DateResultMap () {
        mutex = new ReentrantLock();
        results = new HashMap<String, Long>();
        initialize();
    }
    public void initialize(){
        try {
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.YEAR, -2);
            date = c.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
            String format = formatter.format(date);
            put(format,0L);
            for(int i=0; i<23;i++){
                c.add(Calendar.MONTH, 1);
                date = c.getTime();
                formatter = new SimpleDateFormat("yyyy-MM");
                format = formatter.format(date);
                put(format,0L);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void put(String key,Long value) throws InterruptedException {
        mutex.lock();
        if(results.containsKey(key)){
            results.put(key, results.get(key) + value);
        }else{
            results.put(key, value);
        }
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
    public int size(){
        return results.size();
    }
    public Map<String, Long> getMap(){
        return results;
    }
}

