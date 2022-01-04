package com.libumu.mubook.api;


import com.libumu.mubook.security.MyUserDetails;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    UserDao userDao;

    @GetMapping(path = {"/", "/index"})
    public String home(){
        User user = userDao.getUser("admin");
        return "index";
    }

    @GetMapping("/login")
    public String login(){ return "login"; }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "redirect:/login";
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
    public String mainPage(Model model, Authentication auth){

        //From the HTML (using Thymleaf) the UserDetails methods can only be accessed
        //That's why to access other columns apart from the default ones we cast
        //the "principal" to "MyUserDetails" to be able to access to other columns

        MyUserDetails userDetails= (MyUserDetails) auth.getPrincipal();
        model.addAttribute("loggedUser", userDetails.getUser());
        return "mainPage";
    }
}
