package com.libumu.mubook.api;

import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.news.NewsDao;

import org.springframework.beans.factory.annotation.Autowired;
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
