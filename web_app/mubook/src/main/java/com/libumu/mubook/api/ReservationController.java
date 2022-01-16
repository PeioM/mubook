package com.libumu.mubook.api;

import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.Item;
import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

import com.libumu.mubook.dao.item.ItemDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.reservation.ReservationDao;
import com.libumu.mubook.entities.Reservation;
import com.libumu.mubook.mt.Buffer;

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
    private Buffer buffer = new Buffer(MAXBUFFER);

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Reservation> getActiveReservations(@RequestParam("username") String username,
                                                                     @RequestParam("itemModelName") String itemModelName,
                                                                     @RequestParam("active") Boolean active,
                                                                     Model model){
        Date now = new Date();
        java.sql.Date date = new java.sql.Date(now.getTime());

        if(itemModelName.equals("") && !username.equals("") && !active){
            return reservationDao.findAllByUserUsername(username);
        }else if(username.equals("") && !itemModelName.equals("") && !active){
            return reservationDao.findAllByItemItemModelName(itemModelName);
        }else if(username.equals("") && itemModelName.equals("") && !active){
            return reservationDao.getAllReservations();
        }else if(itemModelName.equals("") && !username.equals("") && active){
            return reservationDao.findAllByUserUsernameAndEndDateIsAfter(username, date);
        }else if(username.equals("") && !itemModelName.equals("") && active){
            return reservationDao.findAllByItemItemModelNameAndEndDateIsAfter(itemModelName, date);
        }else{
            return reservationDao.findAllByEndDateIsAfter(date);
        }
    }

    @GetMapping(path="/reserves")
    public ModelAndView getUserReservations(@RequestParam("type") String type,
                                                                    @RequestParam("active") Boolean active, Model model){
        List<Reservation> reservations;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        Date now = new Date();
        java.sql.Date date = new java.sql.Date(now.getTime());

        if(type.equals("") && !active){
            reservations = reservationDao.findAllByUserUsername(user.getUsername());
        }else if(!type.equals("") && !active){
            reservations = reservationDao.findAllByItemItemModelItemTypeDescriptionAndUserUsername(type, user.getUsername());
        }else if(!type.equals("") && active){
            reservations = reservationDao.findAllByItemItemModelItemTypeDescriptionAndEndDateIsAfterAndUserUsername(type, date, user.getUsername());
        }else{
            reservations = reservationDao.findAllByUserUsernameAndEndDateIsAfter(user.getUsername(), date);
        }

        model.addAttribute("reserves", reservations);

        return new ModelAndView("myReservations", new ModelMap(model));
    }

    @GetMapping(path="/reserve")
    public Reservation getReservation(@RequestParam("reservationId") long reservationId){
        return reservationDao.getReservation(reservationId);
    }

    @GetMapping(path="/offer")
    public ModelAndView makeReservationOffer(ItemModel itemModel, Model model){
        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);*/

        User user = userDao.getUserByUsername("jon"); //Esto luego se coge del spring
        if(itemModel.getItemModelId() == null){
            itemModel = itemModelDao.getItemModel(1); //Esto luego se manda al controlador
        }
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
        c.add(Calendar.DATE, 10);
        Date endDate = c.getTime();

        Reservation reservation = new Reservation();

        reservation.setItem(item);
        reservation.setInitDate(new java.sql.Date(initDate.getTime()));
        reservation.setEndDate(new java.sql.Date(endDate.getTime()));
        reservation.setUser(user);

        model.addAttribute("reserve", reservation);

        return new ModelAndView("reservation", new ModelMap(model));
    }

    @GetMapping(path="/reservation")
    public @ResponseBody Reservation getActiveReservations(@RequestParam("reservationId") long reservationId,
                                                                     Model model){
        return reservationDao.getReservation(reservationId);
    }

    @PostMapping(path="/add")
    public String addReservation(Reservation reservation, Model model){
        reservationDao.addReservation(reservation);

        return "redirect:/index";
    }

    @PostMapping(path="/edit")
    public String editReservation(Reservation reservation, Model model){
        reservationDao.editReservation(reservation);

        return "redirect:/index";
    }

    @PostMapping(path="/delete")
    public String deleteReservation(@RequestParam("reservationId") long reservationId,
                                                           Model model){
        reservationDao.deleteReservation(reservationId);

        return "redirect:/index";
    }

    @GetMapping(path="/itemType")
    public String countReservationByType(Model model){
        long start = System.currentTimeMillis();
        List<Object[]> itemTypeId = itemTypeDao.getAllItemTypeId();
        List<String> key = new ArrayList<>();
        List<Long> value = new ArrayList<>();
        ReservationByType[] rbt = new ReservationByType[MAXNUMTHREADS];
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
