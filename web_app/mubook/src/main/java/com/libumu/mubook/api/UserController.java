package com.libumu.mubook.api;

import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(path="/user")
public class UserController {
    @Autowired
    private UserDao userDao;

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

}