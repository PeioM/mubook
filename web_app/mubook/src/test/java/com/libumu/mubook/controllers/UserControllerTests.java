package com.libumu.mubook.controllers;

import com.libumu.mubook.dao.incidence.IncidenceDao;
import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.Incidence;
import com.libumu.mubook.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IncidenceDao incidenceDao;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testSearchUser() throws Exception {
        mvc.perform(get("/user"))
                .andExpect(model().attributeExists("userTypes"))
                .andExpect(view().name("searchUser"));
    }

    /*@Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testRegister() throws Exception {
        MockMultipartFile file;
        file = new MockMultipartFile(
                "dniImg",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        mvc.perform(multipart("/user/add").file(file).with(csrf())
                .param("name", "Testing")
                .param("surname", "Testing")
                .param("DNI", "1234")
                .param("bornDate", "2001-04-17")
                .param("email", "testing@gmail.com")
                .param("password", "testing")
                .param("username", "testing")
                .param("userType.userTypeId", "USER")
                .param("userActivity.userActivityId", "1"));

        User userCreated = userDao.getUserByUsername("testing");
        assertEquals(userCreated.getUsername(), "testing");
    }*/

    //WebRequest como pasar parametros
    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void createUserWorker() throws Exception {
        mvc.perform(post("/user/create").with(csrf())
                .param("name", "Testing")
                .param("surname", "Testing")
                .param("DNI", "72599397N")
                .param("bornDate", "2001-04-17")
                .param("email", "jonastialo15@gmail.com")
                .param("password", "testing")
                .param("username", "jonastialo")
                .param("userType.userTypeId", "USER")
                .param("userActivity.userActivityId", "1"));

        mvc.perform(post("/user/create").with(csrf())
                .param("name", "Testing")
                .param("surname", "Testing")
                .param("DNI", "123")
                .param("bornDate", "2001-04-17")
                .param("email", "testing@gmail.com")
                .param("password", "testing")
                .param("username", "testing")
                .param("userType.userTypeId", "USER")
                .param("userActivity.userActivityId", "1"));

        User userCreated = userDao.getUserByUsername("testing");
        assertEquals(userCreated.getUsername(), "testing");
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testRegisterGet() throws Exception {
        mvc.perform(get("/user/add"))
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("register"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testAddGet() throws Exception {
        mvc.perform(get("/user/create"))
                .andExpect(model().attributeExists("userEdit"))
                .andExpect(model().attributeExists("action"))
                .andExpect(view().name("editCreateUser"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testUserEditGet() throws Exception {
        mvc.perform(get("/user/3/edit"))
                .andExpect(model().attributeExists("userEdit"))
                .andExpect(model().attributeExists("incidences"))
                .andExpect(model().attributeExists("incidenceSeverities"))
                .andExpect(model().attributeExists("incidence"))
                .andExpect(model().attributeExists("action"))
                .andExpect(view().name("editCreateUser"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testUserProfile() throws Exception {
        mvc.perform(get("/user/profile"))
                .andExpect(model().attributeExists("userEdit"))
                .andExpect(model().attributeExists("incidences"))
                .andExpect(view().name("userProfile"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testAddAndDeleteIncidence() throws Exception {
        mvc.perform(post("/user/incidenceAdd").with(csrf())
                .param("incidenceSeverity.incidenceSeverityId", "1")
                .param("user.userId", "3")
                .param("description", "testing")
                .param("initDate", "2022-01-25")
                .param("endDate", "2022-02-25"));

        Long id = incidenceDao.getTopId();
        Incidence incidenceCreated = incidenceDao.getIncidence(id);
        assertEquals(incidenceCreated.getDescription(), "testing");

        mvc.perform(post("/user/incidenceDelete").with(csrf())
                .param("id", String.valueOf(id)));

        Incidence incidenceDeleted = incidenceDao.getIncidence(id);
        assertNull(incidenceDeleted);

    }
}
