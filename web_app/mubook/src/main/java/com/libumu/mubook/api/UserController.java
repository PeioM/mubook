package com.libumu.mubook.api;

import com.libumu.mubook.dao.incidence.IncidenceDao;
import com.libumu.mubook.dao.incidenceSeverity.IncidenceSeverityDao;
import com.libumu.mubook.dao.incidenceSeverity.IncidenceSeverityDataAccessService;
import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.dao.userActivity.UserActivityDao;
import com.libumu.mubook.dao.userType.UserTypeDao;
import com.libumu.mubook.entities.Incidence;
import com.libumu.mubook.entities.IncidenceSeverity;
import com.libumu.mubook.entities.User;
import com.libumu.mubook.mt.Buffer;

import com.libumu.mubook.security.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigInteger;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping(path="/user")
public class UserController {
    final static int MAXNUMTHREADS = 10;
    final static int MAXBUFFER = 100;
    final static int MAXINCIDENCES = 5;

    public final String LOCAL_UPLOAD_DIR = "C:/IMAGENES_DNI_PRUEBA/";
    public final String SERVER_UPLOAD_DIR = "/home/dniImg/";

    @Autowired
    IncidenceSeverityDao incidenceSeverityDao;
    @Autowired
    IncidenceDao incidenceDao;

    private final UserDao userDao;
    private final UserTypeDao userTypeDao;
    private final UserActivityDao userActivityDao;
    @Autowired
    public UserController(UserDao userDao, UserTypeDao userTypeDao, UserActivityDao userActivityDao) {
        this.userDao = userDao;
        this.userTypeDao = userTypeDao;
        this.userActivityDao = userActivityDao;
    }

    private Buffer buffer = new Buffer(MAXBUFFER);
    private Buffer threadsBuffer = new Buffer(MAXNUMTHREADS);

    int [] ageList = new int []{0, 12, 13, 18, 19, 30, 31, 50, 51, 1000};

    @PostMapping(path="/add")
    public ModelAndView createNewUser (Model model,
        @ModelAttribute User user,
        @RequestParam("dniImg") MultipartFile file,
        WebRequest request) {
        //Return to user form in case there is any error
        String returnStr = "userForm";
        String error = checkUserDuplicated(user);
        if(error.length()==0) {
            if (file == null || file.isEmpty() || file.getOriginalFilename()==null || file.getOriginalFilename().equals("")) {
                error = "Please upload the DNI photo";
            } else if (!passwordsMatch(request)) {
                error = "Password mismatch";
            } else {
                // save the file on the local file system
                String filename = file.getOriginalFilename();
                String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
                try {
                    String pathStr = LOCAL_UPLOAD_DIR + user.getName() + "_" + user.getSurname() + extension;
                    new File(pathStr);  //Create dest file to save
                    Path path = Paths.get(pathStr);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    user.setDniImgPath(path.toString());
                    //In case there is no error redirect to home
                    returnStr = "index";
                } catch (IOException e) {
                    error = "Error uploading file";
                }
                //Obtain encrypted password and save other parameters
                BCryptPasswordEncoder encrypt = new BCryptPasswordEncoder(SecurityConfiguration.ENCRYPT_STRENGTH);
                user.setPassword(encrypt.encode(request.getParameter("password")));
                user.setUserType(userTypeDao.getUserType("USER"));
                user.setUserActivity(userActivityDao.getUserActivity(1));

                userDao.addUser(user);
            }
        }
        //Si se añade usuario notificar informacion sobre el
        //Sino, rellenar campos con los valores introducidos
        model.addAttribute("user", user);
        if(error.length()!=0)model.addAttribute("error",error);

        return new ModelAndView(returnStr, new ModelMap(model));
    }

    @PostMapping(path = "/edit")
    public ModelAndView editUser(Model model,
                                @ModelAttribute User editedUser,
                                 WebRequest request) {

        String error = "";
        if(!passwordsMatch(request)){
            error = error + "Password mismatch";
        }

        if(userDao.countUserByUsernameAndUserIdIsNot(editedUser.getUsername(), editedUser.getUserId()) > 0){
            error = error + " Username already in use";
        }

        if(userDao.countUserByEmailAndUserIdIsNot(editedUser.getEmail(), editedUser.getUserId()) > 0){
            error = error + " Email already in use";
        }

        editedUser.setValidated((Boolean) model.getAttribute("validated"));

        if(model.getAttribute("incidenceSeverity") != null && error.length() == 0){
            IncidenceSeverity incidenceSeverity = incidenceSeverityDao.getIncidenceSeverityByDescription((String) model.getAttribute("incidenceSeverity"));
            String description = (String)model.getAttribute("incidenceDescription");
            Date initDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(initDate);
            c.add(Calendar.MONTH, incidenceSeverity.getDuration());
            Date endDate = c.getTime();
            Incidence incidence = new Incidence();
            incidence.setIncidenceSeverity(incidenceSeverity);
            incidence.setDescription(description);
            incidence.setInitDate(new java.sql.Date(initDate.getTime()));
            incidence.setEndDate(new java.sql.Date(endDate.getTime()));
            incidence.setUser(editedUser);
            incidenceDao.addIncidence(incidence);
        }

        model.addAttribute("user", editedUser);
        if(error.length() > 0){
            model.addAttribute("error", error);
        }else{
            userDao.editUser(editedUser);
        }

        return new ModelAndView("userForm", new ModelMap(model));
    }

    private String checkUserDuplicated(User user){
        String errorStr = "User already exists: ";
        User u = userDao.getUserByUsername(user.getUsername());
        if(userDao.getUserByUsername(user.getUsername())!=null){
            errorStr += "Username already in use";
        }
        else if(userDao.getUserByDNI(user.getDNI())!=null){
            errorStr += "DNI already in use";
        }
        else if(userDao.getUserByEmail(user.getEmail())!=null){
            errorStr += "Email already in use";
        }
        else{
            errorStr = "";
        }
        return errorStr;
    }

    private boolean passwordsMatch(WebRequest request){
        boolean match = true;

        String psw = request.getParameter("password");
        if(psw != null && !psw.equals(request.getParameter("passwordRep"))){
            match = false;
        }
        return match;
    }

    @GetMapping(path="/add")
    public String addNewUser (Model model) {
        model.addAttribute("user", new User());

        return "userForm";
    }

    @GetMapping(path="/edit")
    public String editUser (Model model, @RequestParam("userId") long userId) {
        User user = userDao.getUser(userId);
        model.addAttribute("user", user);

        return "editCreateUser";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers(Model model) {
        // This returns a JSON or XML with the users
        return userDao.getAllUsers();
    }

    @GetMapping(path="/age")
    public String countUsersByAge(Model model){
        long start = System.currentTimeMillis();
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

        long end = System.currentTimeMillis();

        System.out.println((end - start + "ms"));

        return "chart";
    }

    @GetMapping(path="/ageWithoutMT")
    public String countUsersByAgeWithoutMT(Model model){
        long start = System.currentTimeMillis();
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

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

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
        long start = System.currentTimeMillis();
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

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

        return "chart";
    }

    @GetMapping(path="/incidenceWithoutMT")
    public String countUsersByIncidenceWithoutMT(Model model){
        long start = System.currentTimeMillis();
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

        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");

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