package com.libumu.mubook.api;

import com.libumu.mubook.dao.incidence.IncidenceDao;
import com.libumu.mubook.dao.incidenceSeverity.IncidenceSeverityDao;
import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.dao.userActivity.UserActivityDao;
import com.libumu.mubook.dao.userType.UserTypeDao;
import com.libumu.mubook.entities.*;

import com.libumu.mubook.security.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping(path = "/user")
public class UserController {
    public final static String SERVER_UPLOAD_DIR = "src/main/resources/static/images/userProfilesCreated/";

    private final IncidenceSeverityDao incidenceSeverityDao;
    private final IncidenceDao incidenceDao;
    private final UserDao userDao;
    private final UserTypeDao userTypeDao;
    private final UserActivityDao userActivityDao;

    @Autowired
    public UserController(UserDao userDao, UserTypeDao userTypeDao, UserActivityDao userActivityDao,
            IncidenceSeverityDao incidenceSeverityDao, IncidenceDao incidenceDao) {
        this.userDao = userDao;
        this.userTypeDao = userTypeDao;
        this.userActivityDao = userActivityDao;
        this.incidenceSeverityDao = incidenceSeverityDao;
        this.incidenceDao = incidenceDao;
    }

    @GetMapping(path = "")
    public String searchUser(Model model) {
        model.addAttribute("userTypes", userTypeDao.getAllUserTypes());
        model.addAttribute("navPage", "userPage");

        return "searchUser";
    }

    @PostMapping(path="/add")
    public ModelAndView createNewUser (Model model,
        @ModelAttribute User user,
        @RequestParam(value = "dniImg", required = false) MultipartFile file,
        WebRequest request) {
        //Return to user form in case there is any error
        String returnStr = "/register";
        String error = checkUserDuplicated(user);
        if (error.length() == 0) {
            if (file == null || file.isEmpty() || file.getOriginalFilename() == null
                    || file.getOriginalFilename().equals("")) {
                error = "Please upload the DNI photo";
            } else if (!passwordsMatch(request)) {
                error = "Password mismatch";
            } else {
                // save the file on the local file system
                String filename = file.getOriginalFilename();
                String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
                try {
                    String pathStr = SERVER_UPLOAD_DIR + user.getName().replace(" ", "_") + "_" + user.getSurname().replace(" ", "_") + extension;
                    new File(pathStr); // Create dest file to save
                    Path path = Paths.get(pathStr);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    String imagePath = "/images/userProfilesCreated/" + user.getName().replace(" ", "_") + "_" + user.getSurname().replace(" ", "_") + extension;
                    user.setDniImgPath(imagePath);
                    // In case there is no error redirect to home
                    returnStr = "/index";
                } catch (IOException e) {
                    error = "Error uploading file";
                }
                // Obtain encrypted password and save other parameters
                BCryptPasswordEncoder encrypt = new BCryptPasswordEncoder(SecurityConfiguration.ENCRYPT_STRENGTH);
                user.setPassword(encrypt.encode(request.getParameter("password")));
                user.setUserType(userTypeDao.getUserType("USER"));
                user.setUserActivity(userActivityDao.getUserActivity(1));

                userDao.addUser(user);
            }
        }
        // Si se añade usuario notificar informacion sobre el
        // Sino, rellenar campos con los valores introducidos
        model.addAttribute("user", user);
        if (error.length() != 0)
            model.addAttribute("error", error);

        return new ModelAndView(returnStr, new ModelMap(model));
    }

    @PostMapping(path = "/create")
    public String createUser(Model model,
            @ModelAttribute User user,
            WebRequest request) {

        String error = "";
        String returnStr = "";
        if (!passwordsMatch(request) || request.getParameter("password").equals("")) {
            error = error + "Wrong password";
        }

        if (userDao.countUsersByUsername(user.getUsername()) > 0 || user.getUsername().equals("")) {
            error = error + " Wrong username";
        }

        if (userDao.countUsersByEmail(user.getEmail()) > 0 || user.getEmail().equals("")) {
            error = error + " Wrong email";
        }

        if (user.getDNI().equals("") || user.getDNI().length() != 9 || userDao.countUserByDNI(user.getDNI()) > 0) {
            error = error + " The DNI is not correct";
        }

        if (user.getBornDate() == null) {
            error = error + " The born date is null";
        }
        String type = request.getParameter("flexRadioDefault");
        if (type == null) {
            error = error + " Select a user type";
        }

        if (error.length() == 0) {
            BCryptPasswordEncoder encrypt = new BCryptPasswordEncoder(SecurityConfiguration.ENCRYPT_STRENGTH);
            user.setPassword(encrypt.encode(request.getParameter("password")));
            user.setUserType(userTypeDao.getUserType(request.getParameter("flexRadioDefault")));
            user.setValidated(true);
            UserActivity ua = userActivityDao.getUserActivity(1);
            user.setUserActivity(ua);
            userDao.addUser(user);
            returnStr = "redirect:/user";
        } else {
            returnStr = "redirect:/user/create?error=" + error;
        }

        return returnStr;
    }

    @PostMapping(path = "/edit")
    public ModelAndView editUser(Model model,
            @ModelAttribute User user,
            WebRequest request) {

        String error = "";
        User bdUser;
        String returnStr = "";

        if (!passwordsMatch(request)) {
            error = error + "Password mismatch";
        } else if (request.getParameter("password").equals("") && request.getParameter("passwordRep").equals("")) {
            bdUser = userDao.findUserByUserId(user.getUserId());
            user.setPassword(bdUser.getPassword());
        } else {
            BCryptPasswordEncoder encrypt = new BCryptPasswordEncoder(SecurityConfiguration.ENCRYPT_STRENGTH);
            user.setPassword(encrypt.encode(request.getParameter("password")));
        }

        if (userDao.countUserByUsernameAndUserIdIsNot(user.getUsername(), user.getUserId()) > 0) {
            error = error + " Username already in use";
        }

        if (userDao.countUserByEmailAndUserIdIsNot(user.getEmail(), user.getUserId()) > 0) {
            error = error + " Email already in use";
        }

        if (userDao.countUserByDNIAndUserIdIsNot(user.getDNI(), user.getUserId()) > 0) {
            error = error + " DNI already in use";
        }

        if (error.length() > 0) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User u = userDao.getUserByUsername(username);

            if (u.getUserId().equals(user.getUserId())) {
                returnStr = "redirect:/user/profile?error=" + error;
            } else {
                returnStr = "redirect:/user/" + user.getUserId() + "/edit?error=" + error;
            }
        } else {
            bdUser = userDao.getUser(user.getUserId());
            user.setUserActivity(bdUser.getUserActivity());
            user.setDniImgPath(bdUser.getDniImgPath());
            user.setDNI(bdUser.getDNI());
            user.setBornDate(bdUser.getBornDate());
            if (bdUser.getUserType().getUserTypeId().equals("ADMIN")) {
                user.setUserType(userTypeDao.getUserType("ADMIN"));
            } else {
                String type = request.getParameter("flexRadioDefault");
                user.setUserType(userTypeDao.getUserType(type));
            }
            user.setValidated(true);
            userDao.editUser(user);
            returnStr = "redirect:/index";
        }

        return new ModelAndView(returnStr, new ModelMap(model));
    }

    private String checkUserDuplicated(User user) {
        String errorStr = "User already exists: ";
        if (userDao.getUserByUsername(user.getUsername()) != null) {
            errorStr += "Username already in use";
        } else if (userDao.getUserByDNI(user.getDNI()) != null) {
            errorStr += "DNI already in use";
        } else if (userDao.getUserByEmail(user.getEmail()) != null) {
            errorStr += "Email already in use";
        } else {
            errorStr = "";
        }
        return errorStr;
    }

    private boolean passwordsMatch(WebRequest request) {
        boolean match = true;

        String psw = request.getParameter("password");
        if (psw != null && !psw.equals(request.getParameter("passwordRep"))) {
            match = false;
        }
        return match;
    }

    @GetMapping(path = "/add")
    public String registerUser(Model model) {
        model.addAttribute("user", new User());

        return "register";
    }

    @GetMapping(path = "/create")
    public String addNewUser(Model model,
            @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("userEdit", new User());
        model.addAttribute("action", "create");
        model.addAttribute("error", error);

        return "editCreateUser";
    }

    @GetMapping(path = "/{userId}/edit")
    public String editUser(Model model, @PathVariable("userId") String userIdStr,
            @RequestParam(value = "error", required = false) String error) {
        User user = userDao.findUserByUserId(Long.parseLong(userIdStr));
        List<Incidence> incidences = incidenceDao.getAllByUser(user);
        List<IncidenceSeverity> incidenceSeverities = incidenceSeverityDao.getAllIncidenceSeverities();
        Incidence incidence = new Incidence();
        model.addAttribute("userEdit", user);
        model.addAttribute("incidences", incidences);
        model.addAttribute("incidenceSeverities", incidenceSeverities);
        model.addAttribute("incidence", incidence);
        model.addAttribute("action", "edit");
        model.addAttribute("error", error);

        return "editCreateUser";
    }

    @GetMapping(path = "/profile")
    public String profileUser(Model model,
            @RequestParam(value = "error", required = false) String error) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);
        List<Incidence> incidences = incidenceDao.getAllByUser(user);
        model.addAttribute("userEdit", user);
        model.addAttribute("incidences", incidences);
        model.addAttribute("error", error);
        return "userProfile";
    }

    @PostMapping(path = "/incidenceAdd")
    public String addIncidence(Model model,
                                     @ModelAttribute Incidence incidence,
                                     WebRequest request){
        String isId = request.getParameter("severity");
        IncidenceSeverity incidenceSeverity;
        incidenceSeverity = incidenceSeverityDao.getIncidenceSeverityByDescription(isId);
        if(isId == null){
            isId = String.valueOf(incidence.getIncidenceSeverity().getIncidenceSeverityId());
            incidenceSeverity = incidenceSeverityDao.getIncidenceSeverity(Integer.parseInt(isId));
        }
        String userId = request.getParameter("userId");
        User user;
        if(userId == null){
            user = userDao.getUser(incidence.getUser().getUserId());
        }else{
            user = userDao.findUserByUserId(Long.parseLong(userId));
        }

        String error = "";
        String returnStr = "";

        if (user != null && incidenceSeverity != null && incidence.getInitDate() != null
                && !incidence.getDescription().equals("")) {
            incidence.setUser(user);
            incidence.setIncidenceSeverity(incidenceSeverity);
            Calendar c = Calendar.getInstance();
            c.setTime(incidence.getInitDate());
            c.add(Calendar.MONTH, incidenceSeverity.getDuration());
            Date endDate = c.getTime();
            incidence.setEndDate(new java.sql.Date(endDate.getTime()));
            Date date = new Date();
            List<Incidence> activeIncidences = incidenceDao
                    .getIncidencesByEndDateIsAfterAndUser_UserId(new java.sql.Date(date.getTime()), user.getUserId());
            updateIncidenceDates(activeIncidences, incidence);
            incidenceDao.addIncidence(incidence);
            returnStr = "redirect:/user/" + user.getUserId() + "/edit";
        }
        else if(user!=null && (incidenceSeverity==null || incidence.getInitDate() == null || incidence.getDescription().equals(""))) {
            error = "Wrong values for incidence";
            returnStr = "redirect:/user/" + user.getUserId() + "/edit?error=" + error;
        }else{
            System.err.println("User is null");
            returnStr = "redirect:/index";
        }
        return returnStr;
    }

    @PostMapping(path = "/incidenceDelete")
    public String deleteIncidence(@RequestParam("id") long incidenceId) {
        Incidence incidence = incidenceDao.getIncidence(incidenceId);
        if (incidence != null) {
            incidenceDao.deleteIncidence(incidence);
            return "redirect:/user/" + incidence.getUser().getUserId() + "/edit";
        }else{
            return "redirect:/index";
        }

        
    }

    public void updateIncidenceDates(List<Incidence> activeIncidences, Incidence lastIncidence) {
        for (Incidence incidence : activeIncidences.toArray(new Incidence[0])) {
            if (incidence.getEndDate().compareTo(lastIncidence.getEndDate()) < 0) {
                incidence.setEndDate(lastIncidence.getEndDate());
                incidenceDao.editIncidence(incidence);
            }
        }
    }

}