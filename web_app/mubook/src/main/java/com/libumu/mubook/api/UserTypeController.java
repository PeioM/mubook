package com.libumu.mubook.api;

import com.libumu.mubook.dao.userType.UserTypeDao;
import com.libumu.mubook.entities.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/userType")
public class UserTypeController {
    @Autowired
    private UserTypeDao userTypeDao;

    @PostMapping(path="/add")
    public @ResponseBody String addNewUser (Model model) {
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<UserType> getAllUsers(Model model) {
        return userTypeDao.getAllUserTypes();
    }

}