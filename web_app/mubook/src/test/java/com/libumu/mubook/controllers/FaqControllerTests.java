package com.libumu.mubook.controllers;

import com.libumu.mubook.dao.faq.FaqDao;
import com.libumu.mubook.entities.Faq;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @Order(1)
    @Test
    public void showFaqs() throws Exception {
        mockMvc.perform(get("/faq"))
                .andExpect(view().name("faq"))
                .andExpect(model().attributeExists("faqs"))
                .andExpect(model().attributeExists("faq"));
    }

    @Order(2)
    @Test
    public void createFaq() throws Exception {
        mockMvc.perform(post("/faq/add").with(csrf())
                .param("question", "Testing?")
                .param("answer", "Testing"));

        Faq faqCreated = faqDao.findByQuestion("Testing?");
        assertEquals(faqCreated.getQuestion(), "Testing?");
    }

    @Order(3)
    @Test
    public void deleteFaq() throws Exception {
        Faq faq = faqDao.findByQuestion("Testing?");
        mockMvc.perform(post("/faq/delete").with(csrf())
                .param("id", String.valueOf(faq.getFaqid())));
        faq = faqDao.findByQuestion("Testing?");
        assertNull(faq);
    }
}