package com.libumu.mubook.api;

import com.libumu.mubook.dao.incidence.IncidenceDao;
import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.Item;
import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

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
    @Autowired
    private IncidenceDao incidenceDao;
    private Buffer buffer = new Buffer(MAXBUFFER);

    @GetMapping(path="/all")
    public ModelAndView getActiveReservations(@RequestParam("username") String username,
                                                                     @RequestParam("itemModelName") String itemModelName,
                                                                     @RequestParam("active") Boolean active,
                                                                     Model model){
        Date now = new Date();
        java.sql.Date date = new java.sql.Date(now.getTime());
        List<Reservation> reservations;

        if(itemModelName.equals("") && !username.equals("") && !active){
            reservations = reservationDao.findAllByUserUsername(username);
        }else if(username.equals("") && !itemModelName.equals("") && !active){
            reservations = reservationDao.findAllByItemItemModelName(itemModelName);
        }else if(username.equals("") && itemModelName.equals("") && !active){
            reservations = reservationDao.getAllReservations();
        }else if(itemModelName.equals("") && !username.equals("") && active){
            reservations = reservationDao.findAllByUserUsernameAndEndDateIsAfter(username, date);
        }else if(username.equals("") && !itemModelName.equals("") && active){
            reservations = reservationDao.findAllByItemItemModelNameAndEndDateIsAfter(itemModelName, date);
        }else{
            reservations = reservationDao.findAllByEndDateIsAfter(date);
        }

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.DATE, -1);

        model.addAttribute("reserves", reservations);
        model.addAttribute("today", c.getTime());

        return new ModelAndView("myReservations", new ModelMap(model));
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
            returnStr = "index";
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
