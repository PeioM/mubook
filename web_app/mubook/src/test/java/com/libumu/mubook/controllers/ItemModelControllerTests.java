package com.libumu.mubook.controllers;

import com.libumu.mubook.dao.comment.CommentDao;
import com.libumu.mubook.dao.item.ItemDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.news.NewsDao;
import com.libumu.mubook.dao.specificationList.SpecificationListDao;
import com.libumu.mubook.entities.Comment;
import com.libumu.mubook.entities.Item;
import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.SpecificationList;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ItemModelControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemModelDao itemModelDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private SpecificationListDao specListDao;

    @Autowired
    private CommentDao commentDao;

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testViewItemModel() throws Exception {
        mvc.perform(get("/itemModel/1/view"))
                .andExpect(model().attributeExists("itemModel"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("username"))
                .andExpect(view().name("item"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testAddItemModel() throws Exception {
        mvc.perform(get("/itemModel/add"))
                .andExpect(model().attributeExists("itemModel"))
                .andExpect(model().attributeExists("itemTypes"))
                .andExpect(model().attributeExists("status"))
                .andExpect(model().attributeExists("action"))
                .andExpect(view().name("editCreateItem"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testEditItemModel() throws Exception {
        mvc.perform(get("/itemModel/1/edit"))
                .andExpect(model().attributeExists("itemModel"))
                .andExpect(model().attributeExists("itemTypes"))
                .andExpect(model().attributeExists("statusList"))
                .andExpect(model().attributeExists("specifications"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attributeExists("specificationList"))
                .andExpect(model().attributeExists("specList"))
                .andExpect(model().attributeExists("action"))
                .andExpect(view().name("editCreateItem"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testUpdateItem() throws Exception {
        mvc.perform(post("/itemModel/disableItem").with(csrf())
                .param("id", "1"));

        Item item = itemDao.getItem(1);
        assertNotNull(item);
    }

    @Test
    @WithMockUser(username = "user", password = "user", authorities = "ROLE_USER")
    public void testCommentAndDelete() throws Exception {
        mvc.perform(post("/itemModel/comment").with(csrf())
                .param("user.userId", "3")
                .param("id", "1")
                .param("content", "testing")
                .param("date", "2022-01-25"));

        Comment commentCreated = commentDao.findCommentByContent("testing");
        assertEquals(commentCreated.getContent(), "testing");

        Long id = commentDao.getTopId();

        mvc.perform(post("/itemModel/deleteComment").with(csrf())
                .param("id", String.valueOf(id)));
        Comment commentDeleted = commentDao.getComment(id);
        assertNull(commentDeleted);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testCreateEditItemModel() throws Exception {
        mvc.perform(post("/itemModel/create").with(csrf())
                .param("description", "")
                .param("name", "")
                .param("identifier", "")
                .param("img", "")
                .param("itemType.itemTypeId", "2"));


        MockMultipartFile file;
        file = new MockMultipartFile(
        "itemImg",
        "hello.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello, World!".getBytes());

        Long id = itemModelDao.getTopId() + 1;

        mvc.perform(multipart("/itemModel/create").file(file).with(csrf())
                .param("description", "testing")
                .param("name", "testing")
                .param("identifier", "testing"+id)
                .param("img", "testing")
                .param("itemType.itemTypeId", "2"));

        ItemModel itemModelCreated = itemModelDao.getItemModel(itemModelDao.getTopId());
        assertEquals(itemModelCreated.getDescription(), "testing");

        mvc.perform(multipart("/itemModel/edit").file(file).with(csrf())
                .param("itemModelId", String.valueOf(id))
                .param("description", "testingEdit")
                .param("name", "testingEdit")
                .param("identifier", "testingEdit"+id)
                .param("img", "testingEdit")
                .param("itemType.itemTypeId", "2"));

        ItemModel itemModelEdit = itemModelDao.getItemModel(itemModelDao.getTopId());
        assertEquals(itemModelEdit.getDescription(), "testingEdit");

        mvc.perform(post("/itemModel/edit").with(csrf())
                .param("itemModelId", String.valueOf(id))
                .param("description", "")
                .param("name", "")
                .param("identifier", "")
                .param("img", "")
                .param("itemType.itemTypeId", "2"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testAddItem() throws Exception {
        Long id = itemDao.getTopId() + 1;

        mvc.perform(post("/itemModel/addItem").with(csrf())
                .param("serialNum", "Testing"+id)
                .param("itemModel.itemModelId", "1")
                .param("status.description", "Available"));

        Item item = itemDao.getItem(id);
        assertEquals(item.getStatus().getDescription(), "Available");
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_ADMIN")
    public void testAddSpec() throws Exception {
        Long id = specListDao.getTopId() + 1;

        mvc.perform(post("/itemModel/addSpecification").with(csrf())
                .param("itemModel.itemModelId", "1")
                .param("specification.specificationId", "1")
                .param("value", "Testing"+id));

        SpecificationList specListCreated = specListDao.findSpecificationListBySpecificationListId(id);
        assertEquals(specListCreated.getValue(), "Testing"+id);

        mvc.perform(post("/itemModel/deleteSpec").with(csrf())
                .param("id", String.valueOf(id)));

        SpecificationList specListDeleted = specListDao.findSpecificationListBySpecificationListId(id);
        assertNull(specListDeleted);

    }
}