package com.libumu.mubook.controllers;

import com.libumu.mubook.dao.reservation.ReservationDao;
import com.libumu.mubook.entities.Reservation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
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
public class ReservationControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ReservationDao reservationDao;

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testActiveReservations() throws Exception {
        mvc.perform(get("/reservations/all"))
                .andExpect(model().attributeExists("filters"))
                .andExpect(view().name("myReservations"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testOfferReservation() throws Exception {
        mvc.perform(get("/reservations/1/offer"))
                .andExpect(model().attributeExists("reserve"))
                .andExpect(model().attributeExists("offer"))
                .andExpect(model().attributeExists("reserve"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(view().name("reservation"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testReservationByWorker() throws Exception {
        mvc.perform(get("/reservations/1/create"))
                .andExpect(model().attributeExists("reserve"))
                .andExpect(view().name("reservation"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testMakeEditAndDeleteReservation() throws Exception {
        mvc.perform(post("/reservations/add").with(csrf())
                .param("user.userId", "3")
                .param("item.itemId", "1")
                .param("initDate", "2022-01-25")
                .param("endDate", "2022-01-30")
                .param("returnDate", "2022-01-30"));

        Reservation reservationCreated = reservationDao.getReservation(reservationDao.getTopId());
        assertEquals(reservationCreated.getUser().getUsername(), "user");

        mvc.perform(post("/reservations/edit").with(csrf())
                .param("reservationId", String.valueOf(reservationDao.getTopId()))
                .param("user.userId", "1")
                .param("item.itemId", "1")
                .param("initDate", "2022-01-25")
                .param("endDate", "2022-01-30")
                .param("returnDate", "2022-01-30"));

        Reservation reservationEdited = reservationDao.getReservation(reservationDao.getTopId());
        assertEquals(reservationEdited.getUser().getUsername(), "admin");

        Long id = reservationDao.getTopId();

        mvc.perform(post("/reservations/delete").with(csrf())
                .param("id", String.valueOf(id)));
        Reservation reservationDeleted = reservationDao.getReservation(id);
        assertNull(reservationDeleted);
    }
}
