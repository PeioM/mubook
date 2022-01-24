package com.libumu.mubook.controllers;

import com.libumu.mubook.dao.faq.FaqDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FaqControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FaqDao faqDao;

    @Test
    public void showFaqs() throws Exception {
        mockMvc.perform(get("/faq"))
                .andExpect(view().name("faq"));
    }
}
