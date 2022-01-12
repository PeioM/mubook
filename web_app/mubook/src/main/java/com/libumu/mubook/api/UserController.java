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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

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
    public String createNewUser (Model model,
                                               @ModelAttribute User user,
                                               @RequestParam("dniImg") MultipartFile file,
                                               WebRequest request) {
        String returnStr;
        returnStr = checkUserDuplicated(user);
        if(returnStr.length()==0){
            if (file.isEmpty()) {
                returnStr = "redirect:/user/add?error=Please upload the DNI photo";
            }
            else if(!passwordsMatch(request)){
                returnStr = "redirect:/user/add?error=Password mismatch";
            }
            else{
                // save the file on the local file system
                String filename = file.getOriginalFilename();
                String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
                try {
                    String pathStr = LOCAL_UPLOAD_DIR + user.getName()+"_"+user.getSurname()+extension;
                    new File(pathStr);  //Create dest file to save
                    Path path = Paths.get(pathStr);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    user.setDniImgPath(path.toString());
                    returnStr = "redirect:/";
                } catch (IOException e) {
                    returnStr = "redirect:/user/add?error=Error uploading file";
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

        return returnStr;
    }

    private String checkUserDuplicated(User user){
        String returnStr = "redirect:/user/add?error=User already exists: ";
        User u = userDao.getUserByUsername(user.getUsername());
        if(userDao.getUserByUsername(user.getUsername())!=null){
            returnStr += "Username already in use";
        }
        else if(userDao.getUserByDNI(user.getDNI())!=null){
            returnStr += "DNI already in use";
        }
        else if(userDao.getUserByEmail(user.getEmail())!=null){
            returnStr += "Email already in use";
        }
        else{
            returnStr = "";
        }
        return returnStr;
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
    public String addNewUser (Model model, HttpServletRequest request) {

        String error = request.getParameter("error");
        if(error != null){
            model.addAttribute("error", error);
        }
        model.addAttribute("user", new User());

        return "userForm";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers(Model model) {
        // This returns a JSON or XML with the users
        return userDao.getAllUsers();
    }

}