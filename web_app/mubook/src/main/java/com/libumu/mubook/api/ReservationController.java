package com.libumu.mubook.api;

import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import com.libumu.mubook.dao.item.ItemDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.reservation.ReservationDao;
import com.libumu.mubook.mt.Buffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/reservations")
public class ReservationController {
    final static int MAXNUMTHREADS = 10;
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
    private Buffer threadsBuffer = new Buffer(MAXNUMTHREADS);

    @GetMapping(path="/itemType")
    public @ResponseBody String countReservationByType(){
        HashMap<String, Long> result = new HashMap<>();
        List<Object[]> itemTypeId = itemTypeDao.getAllItemTypeId();
        ReservationByType rbt[] = new ReservationByType[MAXNUMTHREADS];

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
                result.put(rbt[threadId].getKey(), rbt[threadId].getValue());
                threadsBuffer.put(threadId);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return "redirect:/home";
    }

    @GetMapping(path="/itemTypeWithoutMT")
    public @ResponseBody String countReservationByTypeWithoutMT(){
        HashMap<String, Long> result = new HashMap<>();
        List<Object[]> resultList;
        int i = 0;

        resultList = reservationDao.countReservationsByItemTypeWithoutMT();

        while(i < resultList.size()){
            BigInteger num = (BigInteger) resultList.get(i)[1];
            result.put((String) resultList.get(i)[0], num.longValue());
            i++;
        }

        return "redirect:/home";
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
    public @ResponseBody String countReservationByModel(){
        HashMap<String, Integer> result = new HashMap<>();
        List<Object[]> itemModelId = itemModelDao.getAllItemModelId();
        ReservationByModel rbm[] = new ReservationByModel[MAXNUMTHREADS];
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
                result.put(rbm[threadId].getKey(), rbm[threadId].getValue());
                threadsBuffer.put(threadId);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return "redirect:/home";
    }

    @GetMapping(path="/itemModelWithoutMT")
    public @ResponseBody String countReservationByModelWithoutMT(){

        HashMap<String, Integer> result = new HashMap<>();
        List<Object[]> resultList;
        int i = 0;

        resultList = reservationDao.countReservationsByItemModelWithoutMT();

        while(i < resultList.size()){
            BigInteger num = (BigInteger) resultList.get(i)[1];
            result.put((String) resultList.get(i)[0], num.intValue());
            i ++;
        }

        return "redirect:/home";
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
    public @ResponseBody String countReservationOfModelByMonth(@RequestParam("itemModelId") long itemModelId){
        HashMap<String, Integer> result = new HashMap<>();
        List<Object[]> itemId = itemDao.getItemWithModelId(itemModelId);
        ReservationsByItem rbi[] = new ReservationsByItem[MAXNUMTHREADS];

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
                    result.put(rbi[threadId].getKey(i), rbi[threadId].getValue(i));
                }
                threadsBuffer.put(threadId);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return "redirect:/home";
    }

    @GetMapping(path="/itemMonthWithoutMT")
    public @ResponseBody String countReservationOfModelByMonthWithoutMT(@RequestParam("itemModelId") long itemModelId){
        HashMap<String, Integer> result = new HashMap<>();
        List<Object[]> resultList;
        int i = 0;

        resultList = reservationDao.countReservationsOfItemEachMonthWithoutMT(itemModelId);

        while(i < resultList.size()){
            BigInteger num = (BigInteger) resultList.get(i)[0];
            result.put((String) resultList.get(i)[1], num.intValue());
            i ++;
        }
        
        return "redirect:/home";
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
            return (String) result.get(month)[1];
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

