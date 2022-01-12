package com.libumu.mubook.api;

import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.dao.userActivity.UserActivityDao;
import com.libumu.mubook.dao.userType.UserTypeDao;
import com.libumu.mubook.entities.User;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Controller
@RequestMapping(path="/user")
public class UserController {

    public final String LOCAL_UPLOAD_DIR = "C:/IMAGENES_DNI_PRUEBA/";
    public final String SERVER_UPLOAD_DIR = "/home/dniImg/";

    private final UserDao userDao;
    private final UserTypeDao userTypeDao;
    private final UserActivityDao userActivityDao;
    @Autowired
    public UserController(UserDao userDao, UserTypeDao userTypeDao, UserActivityDao userActivityDao) {
        this.userDao = userDao;
        this.userTypeDao = userTypeDao;
        this.userActivityDao = userActivityDao;
    }

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
                user.setUserActivity(userActivityDao.getUserActivity(3));

                userDao.addUser(user);
            }
        }
        //Si se añade usuario notificar informacion sobre el
        //Sino, rellenar campos con los valores introducidos
        model.addAttribute("user", user);
        if(error.length()!=0)model.addAttribute("error",error);

        return new ModelAndView(returnStr, new ModelMap(model));
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

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers(Model model) {
        // This returns a JSON or XML with the users
        return userDao.getAllUsers();
    }

}