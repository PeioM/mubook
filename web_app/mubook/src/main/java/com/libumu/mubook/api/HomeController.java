package com.libumu.mubook.api;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping(path = {"", "/", "/home", "/index"})
    public String home(Model model){
        return "index";
    }

    @PostMapping(path = "/login")
    public String login(Model model){
        return "mainPage";
    }

}
