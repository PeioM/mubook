package com.libumu.mubook.api;

import com.libumu.mubook.dao.news.NewsDao;
import com.libumu.mubook.entities.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Controller
@RequestMapping("/news")
public class NewsController {
    public final String LOCAL_UPLOAD_DIR = "C:/IMAGENES_NEW_PRUEBA/";

    @Autowired
    NewsDao newsDao;

    @GetMapping(path="/add")
    public String addNew(Model model){
        News news = new News();
        model.addAttribute("new", news);

        return "createNew";
    }

    @PostMapping(path="/add")
    public String createNew(Model model,
                            @ModelAttribute News news,
                            @RequestParam("newImg")MultipartFile file){
        String error="";

        if(news.getTitle().equals("")){
            error = "Tilte is empty";
        }
        if(news.getDescription().equals("")){
            error = error + " Description is empty";
        }
        if(news.getInitDate() == null || news.getEndDate() == null){
            error = error + " Some of the dates are empty";
        }
        if (file == null || file.isEmpty() || file.getOriginalFilename()==null || file.getOriginalFilename().equals("")) {
            error = error + " Please upload the new image";
        }

        if(error.length() == 0){
            String filename = file.getOriginalFilename();
            String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
            try {
                String pathStr = LOCAL_UPLOAD_DIR + news.getTitle() + extension;
                new File(pathStr);  //Create dest file to save
                Path path = Paths.get(pathStr);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                news.setImage(path.toString());
                //In case there is no error redirect to home
            } catch (IOException e) {
                error = error + " Error uploading file";
            }
        }

        return "index";
    }

    @PostMapping("/delete")
    public String deleteNew(@RequestParam("id") long newId){
        newsDao.deleteNews(newId);

        return "index";
    }
}
