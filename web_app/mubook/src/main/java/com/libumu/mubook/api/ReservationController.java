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
import org.springframework.ui.Model;
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

    @GetMapping(path="/reservationItemType")
    public @ResponseBody String countReservationByType(){
        HashMap<String, Integer> result = new HashMap<>();
        List<Integer> itemTypeId = itemTypeDao.getAllItemTypeId();
        ReservationByType rbt[] = new ReservationByType[MAXNUMTHREADS];

        for(int i = 0; i < itemTypeId.size(); i++){
            try {
                buffer.put(itemTypeId.get(i));
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

    @GetMapping(path="/reservationItemModel")
    public @ResponseBody String countReservationByModel(){
        HashMap<String, Integer> result = new HashMap<>();
        List<Integer> itemModelId = itemModelDao.getAllItemModelId();
        ReservationByModel rbm[] = new ReservationByModel[MAXNUMTHREADS];

        for(int i = 0; i < itemModelId.size(); i++){
            try {
                buffer.put(itemModelId.get(i));
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

    @GetMapping(path="/reservationItemMonth")
    public @ResponseBody String countReservationOfModelByMonth(){
        int itemModelId = 1;
        HashMap<Integer, Integer> result = new HashMap<>();
        List<Integer> itemId = itemDao.getItemWithModelId(itemModelId);
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

