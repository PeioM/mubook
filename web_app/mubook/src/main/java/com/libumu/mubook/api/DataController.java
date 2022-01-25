package com.libumu.mubook.api;

import com.libumu.mubook.dao.item.ItemDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.reservation.ReservationDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Map.Entry;

import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.mt.Buffer;
import com.libumu.mubook.mt.DateResultMap;
import com.libumu.mubook.mt.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/data")
public class DataController {
    int [] ageList = new int []{0, 12, 13, 18, 19, 30, 31, 50, 51, 1000};
    String valueStr= "value";
    String chartStr= "chart";

    final static int MAXNUMTHREADS_RESERVATIONS = 5;
    final static int MAXMONTHS_RESERVATIONS = 24;
    final static int MAXINCIDENCES = 5;



    @Autowired
    ReservationDao reservationDao;
    @Autowired
    ItemTypeDao itemTypeDao;
    @Autowired
    ItemModelDao itemModelDao;
    @Autowired
    ItemDao itemDao;
    @Autowired
    UserDao userDao;


    //ItemType
    @GetMapping(path="/reservations/itemType")
    public String countReservationByType(Model model) throws IOException, InterruptedException{
        List<Object[]> itemTypeId = itemTypeDao.getAllItemTypeId();
        ResultMap results= new ResultMap();
        ReservationByType[] rbt = new ReservationByType[MAXNUMTHREADS_RESERVATIONS];
        Buffer buffer = new Buffer(itemTypeId.size());

        for(int i = 0; i < itemTypeId.size(); i++){
                buffer.put((int) itemTypeId.get(i)[0]);
        }
        int numThreads;
        for(numThreads=0; numThreads < itemTypeId.size() && numThreads < MAXNUMTHREADS_RESERVATIONS; numThreads++){
            rbt[numThreads] = new ReservationByType(numThreads,results,buffer);

        }
        for (int i = 0; i<numThreads; i++) {
            rbt[i].start();
        }
        for (int i = 0; i<numThreads; i++) {
                rbt[i].join();
        }

        model.addAttribute("key", results.getKeys().toArray(new String[0]));
        model.addAttribute(valueStr, results.getValues().toArray(new Long[0]));
        model.addAttribute("name", "Reservations of item model each type");
        model.addAttribute("type", "bar");

        return chartStr;
    }

    @GetMapping(path="/reservations/itemTypeWithoutMT")
    public String countReservationByTypeWithoutMT(Model model){
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
        model.addAttribute(valueStr, value.toArray(new Long[0]));
        model.addAttribute("name", "Reservations of item model each type");
        model.addAttribute("type", "bar");

        return chartStr;
    }

    class ReservationByType extends Thread{
        int threadId;
        ResultMap results;
        Buffer buffer;

        public ReservationByType(int threadId, ResultMap results, Buffer buffer){
            this.threadId = threadId;
            this.results = results;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while(!buffer.empty()){
                try {
                    List<Object[]>result = reservationDao.countReservationsByItemType(buffer.get());
                    results.put((String)result.get(0)[0], ((BigInteger) result.get(0)[1]).longValue());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    //ItemModel
    @GetMapping(path="/reservations/itemModel")
    public String countReservationByModel(Model model) throws InterruptedException{
        List<Object[]> itemModelId = itemModelDao.getAllItemModelId();
        ResultMap results= new ResultMap();
        Map<String, Long> tmpResult = new HashMap<String, Long>();
        Map<String, Long> sortedResult = new LinkedHashMap<String, Long>();
        ReservationByModel[] rbm = new ReservationByModel[MAXNUMTHREADS_RESERVATIONS];
        Buffer buffer = new Buffer(itemModelId.size());

        for(int i = 0; i < itemModelId.size(); i++){
                buffer.put(((BigInteger) itemModelId.get(i)[0]).intValue());
        }
        int numThreads;
        for(numThreads=0; numThreads < itemModelId.size() && numThreads < MAXNUMTHREADS_RESERVATIONS; numThreads++){
            rbm[numThreads] = new ReservationByModel(numThreads,results,buffer);

        }
        for (int i = 0; i<numThreads; i++) {
            rbm[i].start();
        }
        for (int i = 0; i<numThreads; i++) {
                rbm[i].join();
        }
        tmpResult.putAll(results.getMap());
        sortedResult.putAll(sortByValue(tmpResult));
        List<String> key = new ArrayList<String>(sortedResult.keySet());
        List<String> firstNElementsKey = key.stream().limit(5).collect(Collectors.toList());
        List<String> lastNElementsKey = key.subList(Math.max(key.size() - 5, 0), key.size());
        List<String> keyList = new ArrayList<String>();
        keyList.addAll(firstNElementsKey);
        keyList.addAll(lastNElementsKey);
        List<Long> value = new ArrayList<Long>(sortedResult.values());
        List<Long> firstNElementsValue = value.stream().limit(5).collect(Collectors.toList());
        List<Long> lastNElementsValue = value.subList(Math.max(value.size() - 5, 0), value.size());
        List<Long> valueList = new ArrayList<Long>();
        valueList.addAll(firstNElementsValue);
        valueList.addAll(lastNElementsValue);

        model.addAttribute("key", keyList.toArray(new String[0]));
        model.addAttribute(valueStr, valueList.toArray(new Long[0]));
        model.addAttribute("name", "Reservations of item model each model");
        model.addAttribute("type", "bar");

        return chartStr;
    }

    public static Map<String, Long>  sortByValue(Map<String, Long> map){
        List<Map.Entry<String, Long>> list = new ArrayList(map.entrySet());
        list.sort(Entry.comparingByValue());
        map = new LinkedHashMap<String, Long>();
        for(Map.Entry<String, Long> each : list) {
            map.put(each.getKey(), each.getValue());
        }
        return map;
    }

    @GetMapping(path="/reservations/itemModelWithoutMT")
    public String countReservationByModelWithoutMT(Model model){
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
        model.addAttribute(valueStr, value.toArray(new Integer[0]));
        model.addAttribute("name", "Reservations of item model each month");
        model.addAttribute("type", "bar");

        return chartStr;
    }

    class ReservationByModel extends Thread{
        int threadId;
        ResultMap results;
        Buffer buffer;

        public ReservationByModel(int threadId, ResultMap results, Buffer buffer){
            this.threadId = threadId;
            this.results = results;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while(!buffer.empty()){
                try {
                    List<Object[]>result = reservationDao.countReservationsByItemModel((long)buffer.get());
                    results.put((String)result.get(0)[0], ((BigInteger) result.get(0)[1]).longValue());
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    //ItemMonth
    @GetMapping(path="/reservations/itemMonth")
    public String countReservationOfModelByMonth(@RequestParam("itemModelId") long itemModelId, Model model) throws InterruptedException{
        List<Object[]> itemId = itemDao.getItemWithModelId(itemModelId);
        DateResultMap results = new DateResultMap();
        Map<String, Long> sortedResult = new TreeMap<String, Long>();
        ReservationsByItem[] rbi = new ReservationsByItem[MAXNUMTHREADS_RESERVATIONS];
        Buffer buffer = new Buffer(itemId.size());

        for(int i = 0; i < itemId.size(); i++){
                buffer.put(((BigInteger) itemId.get(i)[0]).intValue());
        }

        int numThreads;
        for(numThreads=0; numThreads < itemId.size() && numThreads < MAXNUMTHREADS_RESERVATIONS; numThreads++){
            rbi[numThreads] = new ReservationsByItem(numThreads,results,buffer);

        }
        for (int i = 0; i<numThreads; i++) {
            rbi[i].start();
        }
        for (int i = 0; i<numThreads; i++) {
                rbi[i].join();
        }


        sortedResult.putAll(results.getMap());
        List<String> key = new ArrayList<String>(sortedResult.keySet());
        List<Long> value = new ArrayList<Long>(sortedResult.values());

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute(valueStr, value.toArray(new Long[0]));
        model.addAttribute("name", "Reservations of item model each month");
        model.addAttribute("type", "line");

        return chartStr;
    }

    @GetMapping(path="/reservations/itemMonthWithoutMT")
    public String countReservationOfModelByMonthWithoutMT(@RequestParam("itemModelId") long itemModelId, Model model){
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
        model.addAttribute(valueStr, value.toArray(new Integer[0]));
        model.addAttribute("name", "Reservations of item model each month");
        model.addAttribute("type", "line");

        return chartStr;
    }

    class ReservationsByItem extends Thread{
        int threadId;
        DateResultMap results;
        Buffer buffer;

        public ReservationsByItem(int threadId, DateResultMap results, Buffer buffer){
            this.threadId = threadId;
            this.results = results;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while(!buffer.empty()){
                try {
                    List<Object[]>result = reservationDao.countReservationsOfItemEachMonth((long)buffer.get());
                    for(int i = 0; i < MAXMONTHS_RESERVATIONS && i < result.size(); i++){
                        String keyStr = getDate(String.valueOf(result.get(i)[2]),String.valueOf(result.get(i)[1]));
                        Long valueLong =((BigInteger)result.get(i)[0]).longValue();
                        results.put(keyStr, valueLong);
                    }
                } catch (InterruptedException | ParseException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public String getDate(String month,String year) throws ParseException{
            String dateStr=year + "-";
            SimpleDateFormat format = new SimpleDateFormat("MM");
                Date date = format.parse(month);
                if(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() < 10){
                    String newMonth = "0" + month;
                    dateStr = dateStr + newMonth;
                }else{
                    dateStr = dateStr + month;
                }

            return dateStr;
        }
    }


    //User Age
    @GetMapping(path="/user/age")
    public String countUsersByAge(Model model) throws InterruptedException{
        ResultMap results= new ResultMap();
        Map<String, Long> sortedResult = new TreeMap<String, Long>();
        UsersByAge uba[] = new UsersByAge[MAXNUMTHREADS_RESERVATIONS];
        Buffer buffer=new Buffer(ageList.length);
        for(int i = 0; i < ageList.length; i++){
                buffer.put(ageList[i]);
        }
        int numThreads;
        for(numThreads=0; numThreads < ageList.length && numThreads < MAXNUMTHREADS_RESERVATIONS; numThreads++){
            uba[numThreads] = new UsersByAge(numThreads,results,buffer);

        }
        for (int i = 0; i<numThreads; i++) {
            uba[i].start();
        }
        for (int i = 0; i<numThreads; i++) {
                uba[i].join();
        }
        sortedResult.putAll(results.getMap());
        List<String> key = new ArrayList<String>(sortedResult.keySet());
        List<Long> value = new ArrayList<Long>(sortedResult.values());

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute(valueStr, value.toArray(new Long[0]));
        model.addAttribute("name", "Number of users by age");
        model.addAttribute("type", "bar");

        return chartStr;
    }

    @GetMapping(path="/user/ageWithoutMT")
    public String countUsersByAgeWithoutMT(Model model){
        List<String> key = new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        List<Object[]> resultList;
        int i = 0;

        resultList = userDao.countUsersByAgeWithoutMT();

        while(i < resultList.size()){
            BigInteger num = (BigInteger) resultList.get(i)[0];
            key.add((String) resultList.get(i)[1]);
            value.add(num.intValue());
            i++;
        }

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute(valueStr, value.toArray(new Integer[0]));
        model.addAttribute("name", "Number of users by age");
        model.addAttribute("type", "bar");


        return chartStr;
    }

    class UsersByAge extends Thread{
        int threadId;
        ResultMap results;
        Buffer buffer;

        public UsersByAge(int threadId, ResultMap results, Buffer buffer){
            this.threadId = threadId;
            this.results = results;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            List<Integer> range;
            while(!buffer.empty()){
                try {
                    range=buffer.get2Values();
                    int result = userDao.countUsersByAge(range.get(0),range.get(1));
                    results.put(range.get(0) +"-"+range.get(1), Long.valueOf(result));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    //User incidence
    @GetMapping(path="/user/incidence")
    public String countUsersByIncidence(Model model) throws InterruptedException{
        ResultMap results = new ResultMap();
        UsersByIncidence ubi[] = new UsersByIncidence[MAXNUMTHREADS_RESERVATIONS];
        Buffer buffer=new Buffer(MAXINCIDENCES);

        for(int i = 0; i < MAXINCIDENCES; i++){
                buffer.put(i);
        }

        int numThreads;
        for(numThreads=0; numThreads < MAXINCIDENCES && numThreads < MAXNUMTHREADS_RESERVATIONS; numThreads++){
            ubi[numThreads] = new UsersByIncidence(numThreads,results,buffer);

        }
        for (int i = 0; i<numThreads; i++) {
            ubi[i].start();
        }
        for (int i = 0; i<numThreads; i++) {
                ubi[i].join();
        }

        model.addAttribute("key", results.getKeys().toArray(new String[0]));
        model.addAttribute(valueStr, results.getValues().toArray(new Long[0]));
        model.addAttribute("name", "Number of users by incidence");
        model.addAttribute("type", "bar");


        return chartStr;
    }

    @GetMapping(path="/user/incidenceWithoutMT")
    public String countUsersByIncidenceWithoutMT(Model model){
        List<Integer> key = new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        List<Object[]> resultList;
        int i = 0;

        resultList = userDao.countUsersByIncidenceWithoutMT();

        while(i < resultList.size()){
            key.add((int) resultList.get(i)[1]);
            value.add((int) resultList.get(i)[0]);
            i++;
        }

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute(valueStr, value.toArray(new Integer[0]));
        model.addAttribute("name", "Number of users by incidence");
        model.addAttribute("type", "bar");


        return chartStr;
    }

    class UsersByIncidence extends Thread{
        int threadId;
        ResultMap results;
        Buffer buffer;

        public UsersByIncidence(int threadId, ResultMap results, Buffer buffer){
            this.threadId = threadId;
            this.results = results;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while(!buffer.empty()){
                try {
                    int peso=(int)buffer.get();
                    List<Object[]>result = userDao.countUsersByIncidence(peso);
                    if(result.size()==2){
                        results.put(Integer.toString(peso), ((BigInteger) result.get(0)[0]).longValue());
                    }else{
                        results.put(Integer.toString(peso), 0L);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
