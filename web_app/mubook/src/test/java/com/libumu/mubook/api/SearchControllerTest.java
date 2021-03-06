package com.libumu.mubook.api;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
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
