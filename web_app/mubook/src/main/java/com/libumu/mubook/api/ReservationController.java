package com.libumu.mubook.api;

import org.springframework.web.bind.annotation.RequestMapping;

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

    @Autowired
    private ReservationDao reservationDao;
    private ItemTypeDao itemTypeDao;
    private ItemModelDao itemModelDao;
    private ItemDao itemDao;
    private Buffer buffer = new Buffer(MAXBUFFER);
    private Buffer threadsBuffer = new Buffer(MAXNUMTHREADS);

    @GetMapping(path="/itemType")
    public @ResponseBody String countReservationByType(){
        HashMap<String, Integer> result = new HashMap<>();
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
        HashMap<String, Integer> result = new HashMap<>();
        List<Object[]> resultList;
        int i = 0;

        resultList = reservationDao.countReservationsByItemTypeWithoutMT();

        while(i < resultList.size()){
            result.put((String) resultList.get(0)[i], (int) resultList.get(0)[i + 1]);
            i += 2;
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

        public int getValue(){
            return (int) result.get(0)[1];
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
            try {
                buffer.put((int) itemModelId.get(i)[0]);
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
            result.put((String) resultList.get(0)[i], (int) resultList.get(0)[i + 1]);
            i += 2;
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
            result = reservationDao.countReservationsByItemModel(id);
        }

        public String getKey(){
            return (String) result.get(0)[0];
        }

        public int getValue(){
            return (int) result.get(0)[1];
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @GetMapping(path="/itemMonth")
    public @ResponseBody String countReservationOfModelByMonth(@RequestParam("itemModelId") long itemModelId){
        HashMap<Integer, Integer> result = new HashMap<>();
        List<Object> itemId = itemDao.getItemWithModelId(itemModelId);
        ReservationsByItem rbi[] = new ReservationsByItem[MAXNUMTHREADS];

        for(int i = 0; i < itemId.size(); i++){
            try {
                buffer.put(i);
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
                result.put(rbi[threadId].getKey(), rbi[threadId].getValue());
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
        HashMap<Integer, Integer> result = new HashMap<>();
        List<Object[]> resultList;
        int i = 0;

        resultList = reservationDao.countReservationsOfItemEachMonthWithoutMT(itemModelId);

        while(i < resultList.size()){
            result.put((int) resultList.get(0)[i + 1], (int) resultList.get(0)[i]);
            i += 2;
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
            result = reservationDao.countReservationsOfItemEachMonth(id);
        }

        public int getKey(){
            return (int) result.get(0)[1];
        }

        public int getValue(){
            return (int) result.get(0)[0];
        }

        public void setId(int id) {
            this.id = id;
        }

    }
}

