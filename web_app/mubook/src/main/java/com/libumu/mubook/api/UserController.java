package com.libumu.mubook.api;

import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.User;
import com.libumu.mubook.mt.Buffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path="/user")
public class UserController {
    final static int MAXNUMTHREADS = 10;
    final static int MAXBUFFER = 100;
    final static int MAXINCIDENCES = 5;

    @Autowired
    private UserDao userDao;

    private Buffer buffer = new Buffer(MAXBUFFER);
    private Buffer threadsBuffer = new Buffer(MAXNUMTHREADS);

    int [] ageList = new int []{0, 12, 13, 18, 19, 30, 31, 50, 51, 1000};

    @PostMapping(path="/add")
    public @ResponseBody String addNewUser (HttpServletRequest request) {
        User user = new User(request);

        userDao.addUser(user);

        return "redirect:/home";
    }
    @GetMapping(path="/add")
    public String addNewUser () {
        return "userForm";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers(Model model) {
        // This returns a JSON or XML with the users
        return userDao.getAllUsers();
    }

    @GetMapping(path="/age")
    public String countUsersByAge(Model model){
        List<String> key = new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        UsersByAge uba[] = new UsersByAge[MAXNUMTHREADS];

        for(int i = 0; i < ageList.length; i++){
            try {
                buffer.put(ageList[i]);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        for(int i = 0; i < ageList.length / 2 && i < MAXNUMTHREADS; i++){
            uba[i] = new UsersByAge(i);
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
                uba[threadId].setLow(buffer.get());
                uba[threadId].setHigh(buffer.get());
                uba[threadId].run();
                key.add(uba[threadId].getKey());
                value.add(uba[threadId].getValue());
                threadsBuffer.put(threadId);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute("value", value.toArray(new Integer[0]));
        model.addAttribute("name", "Number of users by age");
        model.addAttribute("type", "bar");

        return "chart";
    }

    @GetMapping(path="/ageWithoutMT")
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
        model.addAttribute("value", value.toArray(new Integer[0]));
        model.addAttribute("name", "Number of users by age");
        model.addAttribute("type", "bar");

        return "chart";
    }

    class UsersByAge extends Thread{
        int high, low;
        int threadId;
        int result;

        public UsersByAge(int threadId){
            this.threadId = threadId;
        }

        @Override
        public void run() {
            result = userDao.countUsersByAge(low, high);
        }

        public void setHigh(int high){
            this.high = high;
        }

        public void setLow(int low){
            this.low = low;
        }

        public String getKey(){
            return low + "-" + high;
        }

        public int getValue(){
            return result;
        }
    }

    @GetMapping(path="/incidence")
    public String countUsersByIncidence(Model model){
        List<Integer> key = new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        UsersByIncidence ubi[] = new UsersByIncidence[MAXNUMTHREADS];

        for(int i = 0; i < MAXINCIDENCES; i++){
            try {
                buffer.put(i);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        for(int i = 0; i < MAXINCIDENCES && i < MAXNUMTHREADS; i++){
            ubi[i] = new UsersByIncidence(i);
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
                ubi[threadId].setNumIncidence(buffer.get());
                ubi[threadId].run();
                if(ubi[threadId].getResult().size() == 2){
                    key.add(ubi[threadId].getKey());
                    value.add(ubi[threadId].getValue());
                }
                threadsBuffer.put(threadId);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        model.addAttribute("key", key.toArray(new String[0]));
        model.addAttribute("value", value.toArray(new Integer[0]));
        model.addAttribute("name", "Number of users by incidence");
        model.addAttribute("type", "bar");

        return "chart";
    }

    @GetMapping(path="/incidenceWithoutMT")
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
        model.addAttribute("value", value.toArray(new Integer[0]));
        model.addAttribute("name", "Number of users by incidence");
        model.addAttribute("type", "bar");

        return "chart";
    }

    class UsersByIncidence extends Thread{
        int numIncidence;
        int threadId;
        List<Object[]> result;

        public UsersByIncidence(int threadId){
            this.threadId = threadId;
        }

        public void setNumIncidence(int numIncidence){
            this.numIncidence = numIncidence;
        }

        @Override
        public void run() {
            result = userDao.countUsersByIncidence(numIncidence);
        }

        public int getKey(){
            return (int) result.get(0)[1];
        }

        public int getValue(){
            return (int) result.get(0)[0];
        }

        public List<Object[]> getResult(){
            return this.result;
        }

    }

}