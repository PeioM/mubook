package com.libumu.mubook.api;

import com.libumu.mubook.dao.comment.CommentDao;
import com.libumu.mubook.dao.incidence.IncidenceDao;
import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.lang.invoke.CallSite;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.libumu.mubook.dao.item.ItemDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.reservation.ReservationDao;
import com.libumu.mubook.mt.Buffer;
import com.libumu.mubook.mt.DateResultMap;
import com.libumu.mubook.mt.ResultMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    @Autowired
    private UserDao userDao;
    @Autowired
    private IncidenceDao incidenceDao;
    @Autowired
    private CommentDao commentDao;

    private Buffer buffer = new Buffer(MAXBUFFER);

    @GetMapping(path="/all")
    public String getActiveReservations(Model model){
        Map<ItemType,Set<ItemModel>> filters = new TreeMap<>();
        List<ItemModel> itemModelList = itemModelDao.getAllItemModels();
        Set<ItemModel> itemModels = new TreeSet<>(itemModelList);

        //Create map for ItemType and Models
        for(ItemModel im : itemModels){
            ItemType it = im.getItemType();
            Set<ItemModel> values;
            if(filters.containsKey(it)){
                values = filters.get(it);
                values.add(im);
            }
            else{
                values = new TreeSet<>();
                values.add(im);
                filters.put(it, values);
            }
        }
        model.addAttribute("filters", filters);

        return "myReservations";
    }

    @GetMapping(path="/list")
    public ModelAndView getUserReservations(@RequestParam("itemModelName") String itemModelName,
                                                                    @RequestParam("active") Boolean active, Model model){
        List<Reservation> reservations;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        Date now = new Date();
        java.sql.Date date = new java.sql.Date(now.getTime());

        if(itemModelName.equals("") && !active){
            reservations = reservationDao.findAllByUserUsername(user.getUsername());
        }else if(!itemModelName.equals("") && !active){
            reservations = reservationDao.findAllByItemItemModelNameAndUserUsername(itemModelName, user.getUsername());
        }else if(!itemModelName.equals("") && active){
            reservations = reservationDao.findAllByItemItemModelNameAndEndDateIsAfterAndUserUsername(itemModelName, date, user.getUsername());
        }else{
            reservations = reservationDao.findAllByUserUsernameAndEndDateIsAfter(user.getUsername(), date);
        }
        Calendar c = Calendar.getInstance();
        Date today = new Date();
        c.setTime(today);
        c.add(Calendar.DATE, -1);

        model.addAttribute("reserves", reservations);
        model.addAttribute("today", c.getTime());

        return new ModelAndView("myReservations", new ModelMap(model));
    }

    @GetMapping(path="/{id}/view")
    public ModelAndView getReservation(@PathVariable("id") String reservationId, Model model){
        Reservation reservation = reservationDao.getReservation(Long.parseLong(reservationId));

        model.addAttribute("reserve", reservation);

        return new ModelAndView("reservation", new ModelMap(model));
    }

    @GetMapping(path="/{itemModelId}/offer")
    public ModelAndView makeReservationOffer(Model model,
                                            @PathVariable("itemModelId") String itemModelStr){

        Long itemModelId = Long.parseLong(itemModelStr);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);
        ItemModel itemModel = itemModelDao.getItemModel(itemModelId);
        Item item = null;
        Date initDate = null;
        String error = "";
        String returnStr = "";

        List<Long> itemsWithoutR = reservationDao.getItemsWithoutReservation(itemModel.getItemModelId());
        List<Object[]> result = reservationDao.getFirstReservationDate(itemModel.getItemModelId());
        if(itemsWithoutR.size() != 0){
            item = itemDao.getItem(itemsWithoutR.get(0));
            initDate = new Date();
        }else if(result.size() != 0) {
            initDate = (Date) result.get(0)[0];
            BigInteger itemId = (BigInteger) result.get(0)[1];
            item = itemDao.getItem(itemId.longValue());
        }else{
            error = "No está disponible este modelo";
        }

        if(error.length() == 0){
            int numIncidence = incidenceDao.countSumIncidenceByUserId(user.getUserId());;
            if(numIncidence >= 3){
                error = "Tienes mas de 3 incidencias acumuladas actualmente";
            }
        }

        if(error.length() == 0){
            Date actualDate = new Date();
            if(initDate.compareTo(actualDate) < 0){
                initDate = actualDate;
            }

            Calendar c = Calendar.getInstance();
            c.setTime(initDate);
            c.add(Calendar.DATE, 10);
            Date endDate = c.getTime();

            Reservation reservation = new Reservation();

            reservation.setItem(item);
            reservation.setInitDate(new java.sql.Date(initDate.getTime()));
            reservation.setEndDate(new java.sql.Date(endDate.getTime()));
            reservation.setUser(user);

            model.addAttribute("reserve", reservation);
            model.addAttribute("offer", "offer");
            returnStr = "reservation";
        }else{
            List<Comment> comments = commentDao.getAllComentsByItemModelId(itemModelId);
            model.addAttribute("error", error);
            model.addAttribute("itemModel", itemModel);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("commentItem", new Comment());
            model.addAttribute("comments", comments);

            returnStr = "item";
        }

        return new ModelAndView(returnStr, new ModelMap(model));
    }

    @GetMapping(path="/{itemModelId}/create")
    public ModelAndView makeReservationWorker(Model model,
                                            @PathVariable("itemModelId") String itemModelStr){

        Long itemModelId = Long.parseLong(itemModelStr);
        
        ItemModel itemModel = itemModelDao.getItemModel(itemModelId);

        List<Object[]> result = reservationDao.getFirstReservationDate(itemModel.getItemModelId());
        Date initDate = (Date) result.get(0)[0];
        BigInteger itemId = (BigInteger) result.get(0)[1];
        Item item = itemDao.getItem(itemId.longValue());

        Date actualDate = new Date();
        if(initDate.compareTo(actualDate) < 0){
            initDate = actualDate;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(initDate);

        Reservation reservation = new Reservation();

        reservation.setItem(item);
        reservation.setInitDate(new java.sql.Date(initDate.getTime()));

        model.addAttribute("reserve", reservation);

        return new ModelAndView("reservation", new ModelMap(model));
    }

    @PostMapping(path="/add")
    public String addReservation(Model model,
                                    @ModelAttribute Reservation reservation){
        reservation.setItem(itemDao.getItem(reservation.getItem().getItemId()));                                 
        reservation.setUser(userDao.getUser(reservation.getUser().getUserId()));                             
        reservationDao.addReservation(reservation);

        return "redirect:/index";
    }

    @PostMapping(path="/edit")
    public String editReservation(Reservation reservation, Model model){
        reservationDao.editReservation(reservation);

        return "redirect:/index";
    }

    @PostMapping(path="/delete")
    public String deleteReservation(@RequestParam("id") long reservationId){
        reservationDao.deleteReservation(reservationId);

        return "redirect:/reservations/list?itemModelName=&active=true";
    }

    @GetMapping(path="/itemType")
    public String countReservationByType(Model model){
        long start = System.currentTimeMillis();
        List<Object[]> itemTypeId = itemTypeDao.getAllItemTypeId();
        ResultMap results= new ResultMap();
        ReservationByType[] rbt = new ReservationByType[MAXNUMTHREADS];
        Buffer buffer = new Buffer(itemTypeId.size());

        for(int i = 0; i < itemTypeId.size(); i++){
            try {
                buffer.put((int) itemTypeId.get(i)[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int numThreads;
        for(numThreads=0; numThreads < itemTypeId.size() && numThreads < MAXNUMTHREADS; numThreads++){
            rbt[numThreads] = new ReservationByType(numThreads,results,buffer);
            
        }
        for (int i = 0; i<numThreads; i++) {
			rbt[i].start();
		}
        for (int i = 0; i<numThreads; i++) {
            try {
                rbt[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        model.addAttribute("key", results.getKeys().toArray(new String[0]));
        model.addAttribute("value", results.getValues().toArray(new Long[0]));
        model.addAttribute("name", "Reservations of item model each type");
        model.addAttribute("type", "bar");
        

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
        model.addAttribute("name", "Reservations of item model each type");
        model.addAttribute("type", "bar");

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

        return "chart";
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
                    e.printStackTrace();
                }
            }
        }
    }
/*
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
    }*/

    @GetMapping(path="/itemModel")
    public String countReservationByModel(Model model){
        long start = System.currentTimeMillis();
        List<Object[]> itemModelId = itemModelDao.getAllItemModelId();
        ResultMap results= new ResultMap();
        Map<String, Long> tmpResult = new HashMap<String, Long>();
        Map<String, Long> sortedResult = new LinkedHashMap<String, Long>();
        ReservationByModel[] rbm = new ReservationByModel[MAXNUMTHREADS];
        Buffer buffer = new Buffer(itemModelId.size());

        for(int i = 0; i < itemModelId.size(); i++){
            try {
                buffer.put(((BigInteger) itemModelId.get(i)[0]).intValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int numThreads;
        for(numThreads=0; numThreads < itemModelId.size() && numThreads < MAXNUMTHREADS; numThreads++){
            rbm[numThreads] = new ReservationByModel(numThreads,results,buffer);
            
        }
        for (int i = 0; i<numThreads; i++) {
			rbm[i].start();
		}
        for (int i = 0; i<numThreads; i++) {
            try {
                rbm[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        model.addAttribute("value", valueList.toArray(new Long[0]));
        model.addAttribute("name", "Reservations of item model each model");
        model.addAttribute("type", "bar");
        

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

        return "chart";
    }
    public static Map<String, Long>  sortByValue(Map<String, Long> map){
        List<Entry<String, Long>> list = new ArrayList(map.entrySet());
        list.sort(Entry.comparingByValue());
        map = new LinkedHashMap<String, Long>();
        for(Entry<String, Long> each : list) {
        map.put(each.getKey(), each.getValue());
        }
        return map;
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
                    e.printStackTrace();
                }
            }
        }
    }
/*
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
    }*/
    @GetMapping(path="/itemMonth")
    public String countReservationOfModelByMonth(@RequestParam("itemModelId") long itemModelId, Model model){
        long start = System.currentTimeMillis();
        List<Object[]> itemId = itemDao.getItemWithModelId(itemModelId);
        DateResultMap results = new DateResultMap();
        Map<String, Long> sortedResult = new TreeMap<String, Long>();
        ReservationsByItem rbi[] = new ReservationsByItem[MAXNUMTHREADS];
        Buffer buffer = new Buffer(itemId.size());

        for(int i = 0; i < itemId.size(); i++){
            try {
                buffer.put(((BigInteger) itemId.get(i)[0]).intValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int numThreads;
        for(numThreads=0; numThreads < itemId.size() && numThreads < MAXNUMTHREADS; numThreads++){
            rbi[numThreads] = new ReservationsByItem(numThreads,results,buffer);
            
        }
        for (int i = 0; i<numThreads; i++) {
			rbi[i].start();
		}
        for (int i = 0; i<numThreads; i++) {
            try {
                rbi[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        

        

        sortedResult.putAll(results.getMap());
        List<String> key = new ArrayList<String>(sortedResult.keySet());
        List<Long> value = new ArrayList<Long>(sortedResult.values());

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute("value", value.toArray(new Long[0]));
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
                        for(int i = 0; i < MAXMONTHS && i < result.size(); i++){
                            String keyStr = getDate(String.valueOf(result.get(i)[2]),String.valueOf(result.get(i)[1]));
                            Long valueLong =((BigInteger)result.get(i)[0]).longValue();
                            results.put(keyStr, valueLong);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }

        public String getDate(String month,String year){
            String dateStr=year + "-";
            SimpleDateFormat format = new SimpleDateFormat("MM");
            try {
                Date date = format.parse(month);
                if(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() < 10){
                    String newMonth = "0" + month.substring(0);
                    dateStr = dateStr + newMonth;
                }else{
                    dateStr = dateStr + month;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return dateStr;
        }
    }
}
