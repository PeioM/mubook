package com.libumu.mubook.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libumu.mubook.dao.buttonClick.ButtonClickDao;
import com.libumu.mubook.entities.User;
import com.libumu.mubook.entitiesAsClasses.ItemModelClass;
import com.libumu.mubook.entitiesAsClasses.ReservationClass;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AjaxControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ButtonClickDao buttonClickDao;

    //SEARCH
    @Test
    public void searchReturnsItemModels() throws Exception {
        //Search Book works
        MvcResult result =  mockMvc.perform(get("/ajax/filterItemModels/Book/1"))
                        .andReturn();
        String content = result.getResponse().getContentAsString();
        Type listType = new TypeToken<ArrayList<ItemModelClass>>(){}.getType();
        List<ItemModelClass> itemModelClassList = new Gson().fromJson(content, listType);
        assertTrue(itemModelClassList.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(itemModelClassList.get(0).getItemType().getDescription(), "Book");


        //All ItemModel search works
        result =  mockMvc.perform(get("/ajax/filterItemModels/all/1"))
                .andReturn();
        content = result.getResponse().getContentAsString();
        itemModelClassList = new Gson().fromJson(content, listType);
        assertTrue(itemModelClassList.size() <= AjaxController.ITEMS_PER_PAGE);
        assertNotNull(itemModelClassList.get(0).getName());


        //Search Book with filters
        String editorial = "Ander Editorial";
        int specificationId = 3;
        result =  mockMvc.perform(get("/ajax/filterItemModels/Book/1")
                        .param(String.valueOf(3)+"[]", editorial))
                .andReturn();
        content = result.getResponse().getContentAsString();
        itemModelClassList = new Gson().fromJson(content, listType);
        assertTrue(itemModelClassList.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(itemModelClassList.get(0).getSpecificationLists().
                get(specificationId-1).getValue(), editorial);

    }

    @Test
    public void searchReturnsPages() throws Exception {
        //Search Book works
        MvcResult result =  mockMvc.perform(get("/ajax/filterItemModelsGetPages/Book"))
                .andReturn();
        assertReturnIsDouble(result);


        //All ItemModel search works
        result =  mockMvc.perform(get("/ajax/filterItemModelsGetPages/all"))
                .andReturn();
        assertReturnIsDouble(result);


        //Search Book with filters
        String editorial = "Ander Editorial";
        int specificationId = 3;
        result =  mockMvc.perform(get("/ajax/filterItemModelsGetPages/Book")
                        .param(String.valueOf(3)+"[]", editorial))
                .andReturn();
        assertReturnIsDouble(result);
    }


    //USERS
    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void searchUsers() throws Exception{
        //All users list works
        MvcResult result = mockMvc.perform(get("/ajax/filterUsers/-/1")
                        .param("containStr",""))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Type listType = new TypeToken<ArrayList<User>>(){}.getType();
        List<User> users = new Gson().fromJson(content, listType);
        assertTrue(users.size() <= AjaxController.ITEMS_PER_PAGE);
        assertNotNull(users.get(0).getUsername());

        //Filter by User type working
        result = mockMvc.perform(get("/ajax/filterUsers/USER/1")
                        .param("containStr",""))
                .andReturn();
        content = result.getResponse().getContentAsString();
        users = new Gson().fromJson(content, listType);
        assertTrue(users.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(users.get(0).getUserType().getUserTypeId(), "USER");
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void searchUserPages() throws Exception{
        //All users page count works
        MvcResult result = mockMvc.perform(get("/ajax/filterUsersGetPages/-")
                        .param("containStr",""))
                .andReturn();
        assertReturnIsDouble(result);

        //Filter by User type page count working
        result = mockMvc.perform(get("/ajax/filterUsersGetPages/USER")
                        .param("containStr",""))
                .andReturn();
        assertReturnIsDouble(result);
    }


    //RESERVATIONS
    //AS ADMIN
    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void searchAllReservationsAsAdmin() throws Exception{
        //All reservations
        MvcResult result = mockMvc.perform(get("/ajax/filterReservations/-/1")
                        .param("active","false"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Type listType = new TypeToken<ArrayList<ReservationClass>>(){}.getType();
        List<ReservationClass> reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertNotNull(reservations.get(0).getInitDate());


        //All reservations by Item Type
        result = mockMvc.perform(get("/ajax/filterReservations/itemType-1/1")
                        .param("active","false"))
                .andReturn();
        content = result.getResponse().getContentAsString();
        reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(reservations.get(0).getItem().getItemModel().getItemType().getItemTypeId(), 1);


        //All reservations by Item Model
        result = mockMvc.perform(get("/ajax/filterReservations/1/1")
                        .param("active","false"))
                .andReturn();
        content = result.getResponse().getContentAsString();
        reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(reservations.get(0).getItem().getItemModel().getItemModelId(), 1);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void searchActiveReservationsAsAdmin() throws Exception{
        //All reservations
        MvcResult result = mockMvc.perform(get("/ajax/filterReservations/-/1")
                        .param("active","true"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Type listType = new TypeToken<ArrayList<ReservationClass>>(){}.getType();
        List<ReservationClass> reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertTrue(reservations.get(0).getInitDate() < new Date().getTime());


        //All reservations by Item Type
        result = mockMvc.perform(get("/ajax/filterReservations/itemType-1/1")
                        .param("active","true"))
                .andReturn();
        content = result.getResponse().getContentAsString();
        reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(reservations.get(0).getItem().getItemModel().getItemType().getItemTypeId(), 1);
        assertTrue(reservations.get(0).getInitDate() < new Date().getTime());

        //All reservations by Item Model
        result = mockMvc.perform(get("/ajax/filterReservations/1/1")
                        .param("active","true"))
                .andReturn();
        content = result.getResponse().getContentAsString();
        reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(reservations.get(0).getItem().getItemModel().getItemModelId(), 1);
        assertTrue(reservations.get(0).getInitDate() < new Date().getTime());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void searchReservationPagesAsAdmin() throws Exception{
        //All reservations
        MvcResult result = mockMvc.perform(get("/ajax/filterReservationsGetPages/-")
                        .param("active","false"))
                .andReturn();
        assertReturnIsDouble(result);


        //All reservations by Item Type
        result = mockMvc.perform(get("/ajax/filterReservationsGetPages/itemType-1")
                        .param("active","false"))
                .andReturn();
        assertReturnIsDouble(result);;

        //All reservations by Item Model
        result = mockMvc.perform(get("/ajax/filterReservationsGetPages/1")
                        .param("active","false"))
                .andReturn();
        assertReturnIsDouble(result);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void searchActiveReservationPagesAsAdmin() throws Exception{
        //All reservations
        MvcResult result = mockMvc.perform(get("/ajax/filterReservationsGetPages/-")
                        .param("active","true"))
                .andReturn();
        assertReturnIsDouble(result);


        //All reservations by Item Type
        result = mockMvc.perform(get("/ajax/filterReservationsGetPages/itemType-1")
                        .param("active","true"))
                .andReturn();
        assertReturnIsDouble(result);;

        //All reservations by Item Model
        result = mockMvc.perform(get("/ajax/filterReservationsGetPages/1")
                        .param("active","true"))
                .andReturn();
        assertReturnIsDouble(result);
    }

    //AS USER
    @Test
    @WithMockUser(username = "user", password = "user", authorities = "ROLE_USER")
    public void searchAllReservationsAsUser() throws Exception{
        //All reservations
        MvcResult result = mockMvc.perform(get("/ajax/filterReservations/-/1")
                        .param("active","false"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Type listType = new TypeToken<ArrayList<ReservationClass>>(){}.getType();
        List<ReservationClass> reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(reservations.get(0).getUser().getUsername(), "user");


        //All reservations by Item Type
        result = mockMvc.perform(get("/ajax/filterReservations/itemType-1/1")
                        .param("active","false"))
                .andReturn();
        content = result.getResponse().getContentAsString();
        reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(reservations.get(0).getUser().getUsername(), "user");
        assertEquals(reservations.get(0).getItem().getItemModel().getItemType().getItemTypeId(), 1);


        //All reservations by Item Model
        result = mockMvc.perform(get("/ajax/filterReservations/1/1")
                        .param("active","false"))
                .andReturn();
        content = result.getResponse().getContentAsString();
        reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(reservations.get(0).getUser().getUsername(), "user");
        assertEquals(reservations.get(0).getItem().getItemModel().getItemModelId(), 1);
    }

    @Test
    @WithMockUser(username = "user", password = "user", authorities = "ROLE_USER")
    public void searchActiveReservationsAsUser() throws Exception{
        //All reservations
        MvcResult result = mockMvc.perform(get("/ajax/filterReservations/-/1")
                        .param("active","true"))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Type listType = new TypeToken<ArrayList<ReservationClass>>(){}.getType();
        List<ReservationClass> reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertTrue(reservations.get(0).getInitDate() < new Date().getTime());
        assertEquals(reservations.get(0).getUser().getUsername(), "user");



        //All reservations by Item Type
        result = mockMvc.perform(get("/ajax/filterReservations/itemType-1/1")
                        .param("active","true"))
                .andReturn();
        content = result.getResponse().getContentAsString();
        reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(reservations.get(0).getItem().getItemModel().getItemType().getItemTypeId(), 1);
        assertTrue(reservations.get(0).getInitDate() < new Date().getTime());
        assertEquals(reservations.get(0).getUser().getUsername(), "user");


        //All reservations by Item Model
        result = mockMvc.perform(get("/ajax/filterReservations/1/1")
                        .param("active","true"))
                .andReturn();
        content = result.getResponse().getContentAsString();
        reservations = new Gson().fromJson(content, listType);
        assertTrue(reservations.size() <= AjaxController.ITEMS_PER_PAGE);
        assertEquals(reservations.get(0).getItem().getItemModel().getItemModelId(), 1);
        assertTrue(reservations.get(0).getInitDate() < new Date().getTime());
        assertEquals(reservations.get(0).getUser().getUsername(), "user");

    }

    @Test
    @WithMockUser(username = "user", password = "user", authorities = "ROLE_USER")
    public void searchReservationPagesAsUser() throws Exception{
        //All reservations
        MvcResult result = mockMvc.perform(get("/ajax/filterReservationsGetPages/-")
                        .param("active","false"))
                .andReturn();
        assertReturnIsDouble(result);


        //All reservations by Item Type
        result = mockMvc.perform(get("/ajax/filterReservationsGetPages/itemType-1")
                        .param("active","false"))
                .andReturn();
        assertReturnIsDouble(result);;

        //All reservations by Item Model
        result = mockMvc.perform(get("/ajax/filterReservationsGetPages/1")
                        .param("active","false"))
                .andReturn();
        assertReturnIsDouble(result);
    }

    @Test
    @WithMockUser(username = "user", password = "user", authorities = "ROLE_USER")
    public void searchActiveReservationPagesAsUser() throws Exception{
        //All reservations
        MvcResult result = mockMvc.perform(get("/ajax/filterReservationsGetPages/-")
                        .param("active","true"))
                .andReturn();
        assertReturnIsDouble(result);


        //All reservations by Item Type
        result = mockMvc.perform(get("/ajax/filterReservationsGetPages/itemType-1")
                        .param("active","true"))
                .andReturn();
        assertReturnIsDouble(result);;

        //All reservations by Item Model
        result = mockMvc.perform(get("/ajax/filterReservationsGetPages/1")
                        .param("active","true"))
                .andReturn();
        assertReturnIsDouble(result);
    }

    @Test
    public void buttonClickRegisterForGrafanaAsAnonymous() throws Exception {
        long numClicks = buttonClickDao.countButtonClicks();
        MvcResult result = mockMvc.perform(get("/ajax/registerGrafana/10")).andReturn();
        assertEquals(numClicks + 1, buttonClickDao.countButtonClicks());
    }

    @Test
    @WithMockUser(username = "user", password = "user", authorities = "ROLE_USER")
    public void buttonClickRegisterForGrafanaAsUser() throws Exception{
        long numClicks = buttonClickDao.countButtonClicks();
        MvcResult result = mockMvc.perform(get("/ajax/registerGrafana/10")).andReturn();
        assertEquals(numClicks + 1, buttonClickDao.countButtonClicks());
    }

    private void assertReturnIsDouble(MvcResult result) throws UnsupportedEncodingException {
        String pageStr = result.getResponse().getContentAsString();
        assertDoesNotThrow(() -> {
            Double.parseDouble(pageStr);
        }, NumberFormatException.class.toString());
    }
}