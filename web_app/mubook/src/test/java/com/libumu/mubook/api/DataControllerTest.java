package com.libumu.mubook.api;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DataControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testReservationsItemType() throws Exception {
        mvc.perform(get("/data/reservations/itemType"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testReservationsItemTypeWithoutMT() throws Exception {
        mvc.perform(get("/data/reservations/itemTypeWithoutMT"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }

    /*@Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testItemModel() throws Exception {
        mvc.perform(get("/data/reservations/itemModel"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }*/

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testReservationsItemModelWithoutMT() throws Exception {
        mvc.perform(get("/data/reservations/itemModelWithoutMT"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testReservationsItemMonth() throws Exception {
        mvc.perform(get("/data/reservations/itemMonth")
                        .param("itemModelId", "1"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testReservationsItemMonthWithoutMT() throws Exception {
        mvc.perform(get("/data/reservations/itemMonthWithoutMT")
                        .param("itemModelId", "1"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testUsersByAge() throws Exception {
        mvc.perform(get("/data/user/age"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testUsersByAgeWithoutMT() throws Exception {
        mvc.perform(get("/data/user/ageWithoutMT"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testUsersByIncidence() throws Exception {
        mvc.perform(get("/data/user/incidence"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testUsersByIncidenceWithoutMT() throws Exception {
        mvc.perform(get("/data/user/incidenceWithoutMT"))
                .andExpect(model().attributeExists("key"))
                .andExpect(model().attributeExists("value"))
                .andExpect(model().attributeExists("type"))
                .andExpect(model().attributeExists("name"))
                .andExpect(view().name("chart"));
    }
}
