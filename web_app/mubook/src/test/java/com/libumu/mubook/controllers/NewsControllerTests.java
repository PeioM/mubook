package com.libumu.mubook.controllers;

import com.libumu.mubook.dao.news.NewsDao;
import com.libumu.mubook.entities.News;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ModelMap;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NewsControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private NewsDao newsDao;

    @Test
    public void createNewTest() throws Exception{

        mvc.perform(get("/news/add", new ModelMap()))
                .andExpect(view().name("createNew"));

    }

    @Test
    public void addNewTest() throws Exception{
        News news = new News();
        news.setTitle("Testing");
        news.setDescription("Testing");
        Date date = new Date();
        news.setInitDate(new java.sql.Date(date.getTime()));
        news.setEndDate(new java.sql.Date(date.getTime()));
        news.setImage("C:\\Users\\jonas\\Downloads\\home.png");
        File file = new File("C:\\Users\\jonas\\Downloads\\home.png");
        FileInputStream input = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/png", IOUtils.toByteArray(input));

        mvc.perform(post("/news/add").with(csrf())
                .param("title", news.getTitle())
                .param("description", news.getDescription())
                .param("initDate", "2022-01-24")
                .param("endDate", "2022-01-30")
                .param("image", news.getImage()));

        News newCreated = newsDao.getNewsByDescription("Testing");
        assertEquals(newCreated.getTitle(), "Testing");
    }

    @Test
    public void deleteNew() throws Exception {
        News news = newsDao.getNewsByDescription("Testing");
        mvc.perform(post("/news/delete").with(csrf()).param("id", String.valueOf(news.getNewsid())));
        news = newsDao.getNewsByDescription("Testing");
        assertNull(news);
    }
}