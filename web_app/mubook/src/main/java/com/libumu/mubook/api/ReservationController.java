package com.libumu.mubook.api;

import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.libumu.mubook.dao.item.ItemDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.reservation.ReservationDao;
import com.libumu.mubook.mt.Buffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/reservations")
public class ReservationController {
    final static int MAXNUMTHREADS = 5;
    final static int MAXBUFFER = 100;
    final static int MAXMONTHS = 24;

    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    private ItemTypeDao itemTypeDao;
    @Autowired
    private ItemModelDao itemModelDao;
    @Autowired
    private ItemDao itemDao;
    private Buffer buffer = new Buffer(MAXBUFFER);

    @GetMapping(path="/itemType")
    public String countReservationByType(Model model){
        long start = System.currentTimeMillis();
        List<Object[]> itemTypeId = itemTypeDao.getAllItemTypeId();
        List<String> key = new ArrayList<>();
        List<Long> value = new ArrayList<>();
        ReservationByType rbt[] = new ReservationByType[MAXNUMTHREADS];
        Buffer threadsBuffer = new Buffer(MAXNUMTHREADS);

        for(int i = 0; i < itemTypeId.size(); i++){
            try {
                buffer.put((int) itemTypeId.get(i)[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < itemTypeId.size() && i < MAXNUMTHREADS; i++){
            rbt[i] = new ReservationByType(i);
            try {
                threadsBuffer.put(i);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        while(buffer.getBuffer().size() > 0){
            int threadId;
            try {
                threadId = threadsBuffer.get();
                rbt[threadId].setId(buffer.get());
                rbt[threadId].run();
                key.add(rbt[threadId].getKey());
                value.add(rbt[threadId].getValue());
                threadsBuffer.put(threadId);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            model.addAttribute("key", key.toArray(new String[0]));
            model.addAttribute("value", value.toArray(new Long[0]));
            model.addAttribute("name", "Reservations of item model each month");
            model.addAttribute("type", "bar");
        }

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

        return "chart";
    }

    @GetMapping(path="/itemTypeWithoutMT")
    public String countReservationByTypeWithoutMT(Model model){
        long start = System.currentTimeMillis();
        List<Object[]> resultList;
        List<String> key = new ArrayList<>();
        List<Long> value = new ArrayList<>();
        int i = 0;

        resultList = reservationDao.countReservationsByItemTypeWithoutMT();

        while(i < resultList.size()){
            BigInteger num = (BigInteger) resultList.get(i)[1];
            key.add((String) resultList.get(i)[0]);
            value.add(num.longValue());
            i++;
        }

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute("value", value.toArray(new Long[0]));
        model.addAttribute("name", "Reservations of item model each month");
        model.addAttribute("type", "bar");

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

        return "chart";
    }

    class ReservationByType extends Thread{
        int id;
        int threadId;
        List<Object[]> result;

        public ReservationByType(int threadId){
            this.threadId = threadId;
        }

        @Override
        public void run() {
            result = reservationDao.countReservationsByItemType(id);
        }

        public String getKey(){
            return (String) result.get(0)[0];
        }

        public Long getValue(){
            BigInteger num = (BigInteger) result.get(0)[1];
            return num.longValue();
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @GetMapping(path="/itemModel")
    public String countReservationByModel(Model model){
        long start = System.currentTimeMillis();
        List<Object[]> itemModelId = itemModelDao.getAllItemModelId();
        List<String> key = new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        ReservationByModel rbm[] = new ReservationByModel[MAXNUMTHREADS];
        Buffer threadsBuffer = new Buffer(MAXNUMTHREADS);

        for(int i = 0; i < itemModelId.size(); i++){
            BigInteger num = (BigInteger) itemModelId.get(i)[0];
            try {
                buffer.put(num.intValue());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        for(int i = 0; i < itemModelId.size() && i < MAXNUMTHREADS; i++){
            rbm[i] = new ReservationByModel(i);
            try {
                threadsBuffer.put(i);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        while(buffer.getBuffer().size() > 0){
            int threadId;
            try {
                threadId = threadsBuffer.get();
                rbm[threadId].setId(buffer.get());
                rbm[threadId].run();
                key.add(rbm[threadId].getKey());
                value.add(rbm[threadId].getValue());
                threadsBuffer.put(threadId);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute("value", value.toArray(new Integer[0]));
        model.addAttribute("name", "Reservations of item model each month");
        model.addAttribute("type", "bar");

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

        return "chart";
    }

    @GetMapping(path="/itemModelWithoutMT")
    public String countReservationByModelWithoutMT(Model model){
        long start = System.currentTimeMillis();
        List<Object[]> resultList;
        List<String> key = new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        int i = 0;

        resultList = reservationDao.countReservationsByItemModelWithoutMT();

        while(i < resultList.size()){
            BigInteger num = (BigInteger) resultList.get(i)[1];
            key.add((String) resultList.get(i)[0]);
            value.add(num.intValue());
            i ++;
        }

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute("value", value.toArray(new Integer[0]));
        model.addAttribute("name", "Reservations of item model each month");
        model.addAttribute("type", "bar");

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

        return "chart";
    }

    class ReservationByModel extends Thread{
        int id;
        int threadId;
        List<Object[]> result;

        public ReservationByModel(int threadId){
            this.threadId = threadId;
        }

        @Override
        public void run() {
            result = reservationDao.countReservationsByItemModel((long) id);
        }

        public String getKey(){
            return (String) result.get(0)[0];
        }

        public int getValue(){
            BigInteger num = (BigInteger) result.get(0)[1];
            return num.intValue();
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @GetMapping(path="/itemMonth")
    public String countReservationOfModelByMonth(@RequestParam("itemModelId") long itemModelId, Model model){
        long start = System.currentTimeMillis();
        List<Object[]> itemId = itemDao.getItemWithModelId(itemModelId);
        Map<String, Integer> results = new HashMap<String, Integer>();
        Map<String, Integer> sortedResult = new TreeMap<String, Integer>();
        List<String> key = new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        ReservationsByItem rbi[] = new ReservationsByItem[MAXNUMTHREADS];
        Buffer threadsBuffer = new Buffer(MAXNUMTHREADS);

        for(int i = 0; i < itemId.size(); i++){
            BigInteger num = (BigInteger) itemId.get(i)[0];
            try {
                buffer.put(num.intValue());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        for(int i = 0; i < itemId.size() && i < MAXNUMTHREADS; i++){
            rbi[i] = new ReservationsByItem(i);
            try {
                threadsBuffer.put(i);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        while(buffer.getBuffer().size() > 0){
            int threadId;
            try {
                threadId = threadsBuffer.get();
                rbi[threadId].setId(buffer.get());
                rbi[threadId].run();
                for(int i = 0; i < MAXMONTHS && i < rbi[threadId].getResult().size(); i++){
                    String keyStr = rbi[threadId].getKey(i);
                    int valueInt = rbi[threadId].getValue(i);
                    if(results.containsKey(keyStr)){
                        results.put(keyStr, results.get(keyStr) + valueInt);
                    }else{
                        results.put(keyStr, valueInt);
                    }
                }
                threadsBuffer.put(threadId);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        sortedResult.putAll(results);
        key = new ArrayList<String>(sortedResult.keySet());
        value = new ArrayList<Integer>(sortedResult.values());

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute("value", value.toArray(new Integer[0]));
        model.addAttribute("name", "Reservations of item model each month");
        model.addAttribute("type", "line");

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

        return "chart";
    }

    @GetMapping(path="/itemMonthWithoutMT")
    public String countReservationOfModelByMonthWithoutMT(@RequestParam("itemModelId") long itemModelId, Model model){
        long start = System.currentTimeMillis();
        List<Object[]> resultList;
        List<String> key = new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        int i = 0;

        resultList = reservationDao.countReservationsOfItemEachMonthWithoutMT(itemModelId);

        while(i < resultList.size()){
            BigInteger num = (BigInteger) resultList.get(i)[0];
            key.add(String.valueOf(resultList.get(i)[1]) + "-" + String.valueOf(resultList.get(i)[2]));
            value.add(num.intValue());
            i ++;
        }

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute("value", value.toArray(new Integer[0]));
        model.addAttribute("name", "Reservations of item model each month");
        model.addAttribute("type", "line");

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
        
        return "chart";
    }

    class ReservationsByItem extends Thread{
        int id;
        int threadId;
        List<Object[]> result;

        public ReservationsByItem(int threadId){
            this.threadId = threadId;
        }

        @Override
        public void run() {
            result = reservationDao.countReservationsOfItemEachMonth((long)id);
        }

        public String getKey(int month){
            String year =  String.valueOf(result.get(month)[1]);
            String mon = String.valueOf(result.get(month)[2]);
            String dateStr=year + "-";
            SimpleDateFormat format = new SimpleDateFormat("MM");
            try {
                Date date = format.parse(mon);
                if(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() < 10){
                    String newMonth = "0" + mon.substring(0);
                    dateStr = dateStr + newMonth;
                }else{
                    dateStr = dateStr + mon;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return dateStr;
        }

        public int getValue(int month){
            BigInteger num = (BigInteger) result.get(month)[0];
            return (int) num.intValue();
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Object[]> getResult(){
            return this.result;
        }
    }
}
