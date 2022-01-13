package com.libumu.mubook.api;

import com.libumu.mubook.dao.news.NewsDao;
import com.libumu.mubook.security.MyUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.Calendar;
import java.util.Date;

@Controller
public class HomeController implements ServletContextAware {

    private ServletContext servletContext;
    @Autowired
    NewsDao newsDao;

    @GetMapping(path = {"/", "/index", "/home"})
    public String home(Model model){
        updateNews();
        model.addAttribute("news", servletContext.getAttribute("news"));
        return "index";
    }

    @GetMapping("/login")
    public String login(){ return "login"; }

    @GetMapping("/faq")
    public String faq(){ return "faq"; }

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

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private void updateNews() {
        Date lastDate = (Date) servletContext.getAttribute("lastFetchDate");
        boolean sameDay = false;
        if(lastDate != null){
            sameDay = isSameDay(lastDate, new Date());
        }
        //if lastDate is null sameDay will stay as false
        //so no need to check if it is null again
        if(!sameDay) {
            servletContext.setAttribute("lastFetchDate", new Date());
            servletContext.setAttribute("news",  newsDao.getActiveNews());
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }
}
