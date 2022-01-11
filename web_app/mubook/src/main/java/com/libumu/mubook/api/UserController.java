package com.libumu.mubook.api;

import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.dao.userActivity.UserActivityDao;
import com.libumu.mubook.dao.userActivity.UserActivityDataAccessService;
import com.libumu.mubook.dao.userType.UserTypeDao;
import com.libumu.mubook.dao.userType.UserTypeDataAccessService;
import com.libumu.mubook.entities.User;
import com.libumu.mubook.security.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping(path="/user")
public class UserController {

    private final String LOCAL_UPLOAD_DIR = "C:/IMAGENES_DNI_PRUEBA/";
    private final String SERVER_UPLOAD_DIR = "/home/dniImg/";

    private final UserDao userDao;
    private final UserTypeDao userTypeDao;
    private final UserActivityDao userActivityDao;
    @Autowired
    public UserController(UserDao userDao, UserTypeDao userTypeDao, UserActivityDao userActivityDao) {
        this.userDao = userDao;
        this.userTypeDao = userTypeDao;
        this.userActivityDao = userActivityDao;
    }
    //private final String UPLOAD_DIR = "/home/dniImg/";

    @PostMapping(path="/add")
    public String createNewUser (Model model,
                                               @ModelAttribute User user,
                                               @RequestParam("dniImg") MultipartFile file,
                                               WebRequest request) {
        // check if file is empty
        if (file.isEmpty()) {
            return "redirect:/user/add?error=Please upload the DNI photo";
        }
        String psw = request.getParameter("password");
        //Check if password coincides
        if(!psw.equals(request.getParameter("passwordRep"))){
            return "redirect:/user/add?error=Password mismatch";
        }
        // save the file on the local file system
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        try {
            String pathStr = LOCAL_UPLOAD_DIR + user.getName()+"_"+user.getSurname()+extension;
            File dest = new File(pathStr);
            Path path = Paths.get(pathStr);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            user.setDniImgPath(path.toString());
        } catch (IOException e) {
            return "redirect:/user/add?error=Uploaded file not foundUploaded file not found";
        }
        //Obtain encrypted password and save other parameters
        BCryptPasswordEncoder encrypt = new BCryptPasswordEncoder(SecurityConfiguration.ENCRYPT_STRENGTH);
        user.setPassword(encrypt.encode(psw));
        user.setUserType(userTypeDao.getUserType("USER"));
        user.setUserActivity(userActivityDao.getUserActivity(3));

        userDao.addUser(user);

        model.addAttribute("user", user);

        return "redirect:/";
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