package com.libumu.mubook.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTests {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testIndex() throws Exception {
        mvc.perform(get("/"))
                .andExpect(model().attributeExists("news"))
                .andExpect(view().name("index"));

        mvc.perform(get("/index"))
                .andExpect(model().attributeExists("news"))
                .andExpect(view().name("index"));

        mvc.perform(get("/home"))
                .andExpect(model().attributeExists("news"))
                .andExpect(view().name("index"));
    }

    @Test
    public void testLogin() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(view().name("login"));
    }

    @Test
    public void testAboutUs() throws Exception {
        mvc.perform(get("/aboutUs"))
                .andExpect(view().name("aboutUs"));
    }

    @Test
    public void testData() throws Exception {
        mvc.perform(get("/data"))
                .andExpect(model().attributeExists("itemModels"))
                .andExpect(view().name("selectChart"));
    }

}