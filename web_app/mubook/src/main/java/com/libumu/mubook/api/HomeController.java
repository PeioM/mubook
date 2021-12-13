package com.libumu.mubook.api;


import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

@Controller
public class HomeController {

    @Autowired
    private UserDao userDao;

    @GetMapping(path = {"", "/", "/index"})
    public String home(Model model){
        return "index";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "redirect:/login";
    }

    @GetMapping("/home")
    @ResponseBody
    public String home(){
        return "<h1>This is home page</h1>";
    }
    @GetMapping("/normalUser")
    @ResponseBody
    public String user() {
        return ("<h1>Welcome User</h1>");
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin() {
        return ("<h1>Welcome Admin</h1>");
    }

    @GetMapping("/worker")
    @ResponseBody
    public String worker() {
        return ("<h1>Welcome worker</h1>");
    }

    @GetMapping("/mainPage")
    public String mainPage(){ return "mainPage"; }
}
