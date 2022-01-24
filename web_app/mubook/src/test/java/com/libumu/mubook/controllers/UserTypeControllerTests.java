package com.libumu.mubook.controllers;

import com.libumu.mubook.dao.news.NewsDao;
import com.libumu.mubook.dao.userType.UserTypeDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserTypeControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserTypeDao userTypeDao;

    @Test
    public void getUserTypes() throws Exception {
        mvc.perform(get("/userType/all"))
                .andExpect(status().isOk());
    }

    @Test
    public void createUserType() throws Exception {
        mvc.perform(post("/userType/add").with(csrf()))
                .andExpect(status().isOk());
    }
}
