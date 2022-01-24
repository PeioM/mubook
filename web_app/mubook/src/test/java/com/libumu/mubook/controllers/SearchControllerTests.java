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
public class SearchControllerTests {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testSearch() throws Exception {
        mvc.perform(get("/search"))
                .andExpect(model().attributeExists("itemTypes"))
                .andExpect(model().attributeExists("itemModels"))
                .andExpect(view().name("searchItems"));

        mvc.perform(get("/search/Book"))
                .andExpect(model().attributeExists("actualItemType"))
                .andExpect(model().attributeExists("specifications"))
                .andExpect(view().name("searchItems"));
    }
}
