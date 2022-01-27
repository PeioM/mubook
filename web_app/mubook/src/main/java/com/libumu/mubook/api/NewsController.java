package com.libumu.mubook.api;

import com.libumu.mubook.dao.news.NewsDao;
import com.libumu.mubook.entities.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import javax.servlet.ServletContext;

@Controller
@RequestMapping("/news")
public class NewsController implements ServletContextAware {
    public final String NEWS_IMAGES_DIR = HomeController.IMAGE_UPLOAD_DIR + "news/";
    private ServletContext servletContext;
    @Autowired
    NewsDao newsDao;

    @GetMapping(path="/add")
    public String addNew(Model model){
        News news = new News();
        model.addAttribute("newEntity", news);

        return "createNew";
    }

    @PostMapping(path="/add")
    public String createNew(Model model,
                        @ModelAttribute News news,
                        @RequestParam(value = "newImg", required = false) MultipartFile file
                            ){
        String error="";
        if(news.getTitle().equals("")){
            error = "Title is empty";
        }
        if(news.getDescription().equals("")){
            error = error + " Description is empty";
        }
        if(news.getInitDate() == null || news.getEndDate() == null){
            error = error + " Some of the dates are empty";
        }
        if(error.length()==0) {
            if (file == null || file.isEmpty() || file.getOriginalFilename()==null || file.getOriginalFilename().equals("")) {
                error = "Please upload the new image";
            }
            else{
                // save the file on the local file system
                String filename = file.getOriginalFilename();
                String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
                try {
                    String pathStr = NEWS_IMAGES_DIR + news.getTitle().replace(" ", "_") + extension;
                    new File(pathStr);  //Create dest file to save
                    Path path = Paths.get(pathStr);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    String imagePath = "/images/news/" + news.getTitle().replace(" ", "_") + extension;
                    news.setImage(imagePath);
                    //In case there is no error redirect to home
                } catch (IOException e) {
                    error = "Error uploading file";
                }
            }
        }

        newsDao.addNews(news);
        HomeController.updateNews(servletContext, newsDao);

        return "redirect:/index";
    }

    @PostMapping("/delete")
    public String deleteNew(@RequestParam("id") long newId){
        newsDao.deleteNews(newId);

        return "redirect:/index";
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
