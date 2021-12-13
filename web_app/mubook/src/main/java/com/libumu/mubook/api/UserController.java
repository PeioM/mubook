package com.libumu.mubook.api;

import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(path="/user")
public class UserController {
    @Autowired
    private UserDao userDao;

    @PostMapping(path="/add")
    public @ResponseBody String addNewUser (@RequestParam("user") User user, HttpServletRequest request) {
        userDao.addUser(user);
        request.getSession().setAttribute("User", user);
        return "redirect:/home";
    }
    @GetMapping(path="/add")
    public @ResponseBody String addNewUser (HttpServletRequest request) {
        return "userForm";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers(Model model) {
        // This returns a JSON or XML with the users
        return userDao.getAllUsers();
    }

}