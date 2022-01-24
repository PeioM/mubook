package com.libumu.mubook.api;

import com.libumu.mubook.dao.itemModel.ItemModelDao;
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
    @Autowired
    ItemModelDao itemModelDao;

    @GetMapping(path = {"/", "/index", "/home"})
    public String home(Model model){
        
     //   if(isSameDay((Date) servletContext.getAttribute("lastFetchDate"), new Date())){
            updateNews(servletContext, newsDao);
       // }
        model.addAttribute("news", servletContext.getAttribute("news"));
        return "index";
    }

    @GetMapping("/login")
    public String login(){ return "login"; }

    @GetMapping("/aboutUs")
    public String aboutUs(){ return "aboutUs"; }

    @GetMapping("/data")
    public String dataChart(Model model){ 
        model.addAttribute("itemModels", itemModelDao.getAllItemModels());
        return "selectChart"; 
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public static void updateNews(ServletContext servletContext, NewsDao newsDao) {
        servletContext.setAttribute("lastFetchDate", new Date());
        servletContext.setAttribute("news",  newsDao.getActiveNews());
    }


}
