package com.libumu.mubook.api;

import com.libumu.mubook.dao.news.NewsDao;
import com.libumu.mubook.entities.News;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NewsControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private NewsDao newsDao;

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void createNewTest() throws Exception{

        mvc.perform(get("/news/add", new ModelMap()))
                .andExpect(view().name("createNew"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void addNewTest() throws Exception{

        MockMultipartFile file;
        file = new MockMultipartFile(
                "newImg",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        mvc.perform(multipart("/news/add").file(file).with(csrf())
                .param("title", "Testing")
                .param("description", "Testing")
                .param("initDate", "2022-01-24")
                .param("endDate", "2022-01-30")
                .param("image", "image/testing"));

        News newCreated = newsDao.getNewsByDescription("Testing");
        assertEquals(newCreated.getTitle(), "Testing");

        mvc.perform(multipart("/news/add").file(file).with(csrf())
                .param("title", "")
                .param("description", "")
                .param("initDate", "")
                .param("endDate", "")
                .param("image", ""));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void deleteNew() throws Exception {
        News news = newsDao.getNewsByDescription("Testing");
        mvc.perform(post("/news/delete").with(csrf()).param("id", String.valueOf(news.getNewsid())));
        news = newsDao.getNewsByDescription("Testing");
        assertNull(news);
    }
}