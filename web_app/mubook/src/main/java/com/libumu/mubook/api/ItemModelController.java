package com.libumu.mubook.api;

import com.libumu.mubook.dao.comment.CommentDao;
import com.libumu.mubook.dao.item.ItemDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.specification.SpecificationDao;
import com.libumu.mubook.dao.specificationList.SpecificationListDao;
import com.libumu.mubook.dao.status.StatusDao;
import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.*;
import com.libumu.mubook.entities.SpecificationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(path = "/itemModel")
public class ItemModelController {
    public final String ITEMS_IMAGES_DIR = "src/main/resources/static/images/createdItems/";

    private final ItemModelDao itemModelDao;
    private final ItemTypeDao itemTypeDao;
    private final StatusDao statusDao;
    private final ItemDao itemDao;
    private final SpecificationDao specificationDao;
    private final SpecificationListDao specificationListDao;
    private final CommentDao commentDao;
    private final UserDao userDao;
    @Autowired
    public ItemModelController(ItemModelDao itemModelDao, ItemTypeDao itemTypeDao, StatusDao statusDao, ItemDao itemDao, SpecificationDao specificationDao, SpecificationListDao specificationListDao, CommentDao commentDao, UserDao userDao) {
        this.itemModelDao = itemModelDao;
        this.itemTypeDao = itemTypeDao;
        this.statusDao = statusDao;
        this.itemDao = itemDao;
        this.specificationDao = specificationDao;
        this.specificationListDao = specificationListDao;
        this.commentDao = commentDao;
        this.userDao = userDao;
    }

    @GetMapping(path = "/{itemModelId}/view")
    public ModelAndView getItemModel(Model model,
            @PathVariable("itemModelId") String itemModelStr) {
        Long itemModelId = Long.parseLong(itemModelStr);
        ItemModel itemModel = itemModelDao.getItemModel(itemModelId);
        List<Comment> comments = commentDao.getAllComentsByItemModelId(itemModelId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Comment comment = new Comment();

        model.addAttribute("itemModel", itemModel);
        model.addAttribute("comments", comments);
        model.addAttribute("commentItem", comment);
        model.addAttribute("username", username);

        return new ModelAndView("item", new ModelMap(model));
    }

    @GetMapping(path = "/add")
    public String addItemModel(Model model,
            @RequestParam(value = "error", required = false) String error) {
        ItemModel itemModel = new ItemModel();
        List<ItemType> itemTypeList = itemTypeDao.getAllItemTypes();
        List<Status> statusList = statusDao.getAllStatus();

        model.addAttribute("itemModel", itemModel);
        model.addAttribute("itemTypes", itemTypeList);
        model.addAttribute("status", statusList);
        model.addAttribute("action", "create");
        model.addAttribute("error", error);

        return "editCreateItem";
    }

    @GetMapping(path = "{id}/edit")
    public String editItemModel(Model model,
            @PathVariable("id") String idStr,
            @RequestParam(value = "error", required = false) String error) {
        Long itemModelId = Long.parseLong(idStr);
        ItemModel itemModel = itemModelDao.getItemModel(itemModelId);
        List<ItemType> itemTypeList = itemTypeDao.getAllItemTypes();
        List<Status> statusList = statusDao.getAllStatus();
        List<Specification> specifications = specificationDao.getAllSpecifications();
        List<SpecificationList> specList = specificationListDao
                .getSpecificationListsByItemModel_ItemModelId(itemModel.getItemModelId());
        List<Item> itemList = itemDao.getItemByItemModelItemModelId(itemModel.getItemModelId());
        Item item = new Item();

        model.addAttribute("itemModel", itemModel);
        model.addAttribute("itemTypes", itemTypeList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("specifications", specifications);
        model.addAttribute("items", itemList);
        model.addAttribute("item", item);
        model.addAttribute("specificationList", new SpecificationList());
        model.addAttribute("specList", specList);
        model.addAttribute("action", "edit");
        model.addAttribute("error", error);

        return "editCreateItem";
    }

    @PostMapping(path = "/create")
    public String createItemModel(Model model,
            @ModelAttribute ItemModel itemModel,
            @RequestParam(value = "itemImg", required = false) MultipartFile file,
            WebRequest request) {
        String error = "";
        String type = request.getParameter("type");
        ItemType itemType = itemTypeDao.getItemTypeByDesc(type);
        if(type == null){
            itemType = itemTypeDao.getItemType(itemModel.getItemType().getItemTypeId());
        }
        String returnStr = "redirect:/index";

        if (itemType == null) {
            error = error + " Item type is empty";
        }

        if (itemModel.getName().equals("")) {
            error = error + " Item model name is empty";
        }
        if (itemModel.getDescription().equals("")) {
            error = error + " Item model description is empty";
        }
        if (itemModel.getIdentifier().equals("")
                || itemModelDao.countItemModelByIdentifier(itemModel.getIdentifier()) > 0) {
            error = error + " Wrong item model identifier";
        }
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null
                || file.getOriginalFilename().equals("")) {
            error = error + " Please upload the item model image";
        }
        if (error.length() == 0 && file != null) {
            String filename = file.getOriginalFilename();
            String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
            try {
                String pathStr = ITEMS_IMAGES_DIR + itemModel.getName().replace(" ", "_") + extension;
                new File(pathStr); // Create dest file to save
                Path path = Paths.get(pathStr);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                String imagePath = "images/createdItems/" + itemModel.getName().replace(" ", "_") + extension;
                itemModel.setImg(imagePath);
                // In case there is no error redirect to home
            } catch (IOException e) {
                error = error + " Error uploading file";
            }
        }

        if (error.length() == 0) {
            itemModel.setItemType(itemType);
            itemModelDao.addItemModel(itemModel);
        } else {
            returnStr = "redirect:/itemModel/add?error=" + error;
        }

        return returnStr;
    }

    @PostMapping(path = "/addSpecification")
    public String addSpecification(Model model,
            @ModelAttribute SpecificationList specificationList,
            WebRequest request) {
        String error = "";
        String returnStr = "";

        String itemModelId = request.getParameter("itemModelId");
        ItemModel itemModel;
        if(itemModelId == null){
            itemModel = itemModelDao.getItemModel(specificationList.getItemModel().getItemModelId());
        }else{
            itemModel = itemModelDao.getItemModel(Long.parseLong(itemModelId));
        }
        String id = request.getParameter("sp");
        Specification specification;
        if(id == null){
            specification = specificationDao.getSpecification(specificationList.getSpecification().getSpecificationId());
        }else{
            specification = specificationDao.findSpecificationBySpecificationIdIs(Integer.parseInt(id));
        }

        if (itemModel != null && specification != null && !specificationList.getValue().equals("")) {
            specificationList.setItemModel(itemModel);
            specificationList.setSpecification(specification);
            specificationListDao.addSpecificationList(specificationList);
            List<SpecificationList> specList = itemModel.getSpecificationLists();
            specList.add(specificationList);
            itemModel.setSpecificationLists(specList);
            itemModelDao.editItemModel(itemModel);
            returnStr = "redirect:/itemModel/" + itemModel.getItemModelId() + "/edit";
        }if(itemModel != null && (specification==null || specificationList.getValue().equals(""))){
            error = "Incorrect specification values";
            returnStr = "redirect:/itemModel/" + itemModel.getItemModelId() + "/edit?error=" + error;
        }else{
            error = "Major error, probably item model is null";
        }

        return returnStr;
    }

    @PostMapping(path = "/addItem")
    public String addItem(Model model,
            @ModelAttribute Item item,
            WebRequest request) {

        String error = "";
        String returnStr = "";
        String itemModelId = request.getParameter("itemModelId");
        ItemModel itemModel;
        if(itemModelId == null){
            itemModel = itemModelDao.getItemModel(item.getItemModel().getItemModelId());
        }else{
            itemModel = itemModelDao.getItemModel(Long.parseLong(itemModelId));
        }
        String st = request.getParameter("st");
        Status status;
        if(st == null){
            status = statusDao.getStatusByDescription(item.getStatus().getDescription());
        }else{
            status = statusDao.getStatusByDescription(st);
        }
        if (itemModel != null && status != null && !item.getSerialNum().equals("")) {
            item.setItemModel(itemModel);
            item.setStatus(status);
            itemDao.addItem(item);
            returnStr = "redirect:/itemModel/" + itemModel.getItemModelId() + "/edit";
        } if(itemModel != null && (status == null || item.getSerialNum().equals(""))) {
            error = "Incorrect item values";
            returnStr = "redirect:/itemModel/" + itemModel.getItemModelId() + "/edit?error=" + error;
        }else{
            System.out.println("ItenModel is null");
            returnStr = "redirect:/index";
        }

        return returnStr;
    }

    @PostMapping(path = "/deleteSpec")
    public String deleteSpec(@RequestParam("id") long specId) {
        SpecificationList specificationList = specificationListDao.findSpecificationListBySpecificationListId(specId);

        if (specificationList != null) {
            specificationListDao.deleteSpecificationList(specificationList);
            return "redirect:/itemModel/" + specificationList.getItemModel().getItemModelId() + "/edit";
        }else{
            System.out.println("Specification list is null");
            return "redirect:/index";
        }
    }

    @PostMapping(path = "/disableItem")
    public String disableItem(@RequestParam("id") long itemId) {
        Item item = itemDao.getItem(itemId);
        Status available = statusDao.getStatusByDescription("Available");
        Status disable = statusDao.getStatusByDescription("Disable");
        if (item != null) {
            if (item.getStatus().getDescription().equals(available.getDescription())) {
                item.setStatus(disable);
            } else {
                item.setStatus(available);
            }
            itemDao.editItem(item);
            return "redirect:/itemModel/" + item.getItemModel().getItemModelId() + "/edit";
        }else{
            System.out.println("Item is null");
            return "redirect:/index";
        }

        
    }

    @PostMapping(path = "/comment")
    public String commentItemModel(Model model,
            @ModelAttribute Comment comment,
            @RequestParam("id") long itemModelId) {
        String returnStr = "error";
        ItemModel itemModel = itemModelDao.getItemModel(itemModelId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);
        Date date = new Date();

        if (itemModel != null && user != null) {
            comment.setItemModel(itemModel);
            comment.setUser(user);
            comment.setDate(new java.sql.Date(date.getTime()));
            commentDao.addComent(comment);
            returnStr = "redirect:/itemModel/" + itemModelId + "/view";
        }

        return returnStr;
    }

    @PostMapping(path = "/deleteComment")
    public String deleteComment(Model model,
            @RequestParam("id") long id) {
        Comment comment = commentDao.getComment(id);
        long itemModelId = comment.getItemModel().getItemModelId();
        commentDao.deleteComment(id);

        return "redirect:/itemModel/" + itemModelId + "/view";
    }

    @PostMapping(path = "/edit")
    public String editItemModel(Model model,
            @ModelAttribute ItemModel itemModelEdited,
            @RequestParam(value = "itemImg", required = false) MultipartFile file,
            WebRequest request) throws IOException {
        String error = "";
        String returnStr = "";
        ItemType it = itemTypeDao.getItemTypeByDesc(request.getParameter("type"));
        if(it == null){
            it = itemTypeDao.getItemType(itemModelEdited.getItemType().getItemTypeId());
        }
        if (!itemModelEdited.getIdentifier().equals("")) {
            if (itemModelDao.countItemModelByIdentifierAndItemModelIdNotLike(itemModelEdited.getIdentifier(),
                    itemModelEdited.getItemModelId()) > 0) {
                error = error + " Item model identifier already exists";
            }
        } else {
            error = error + " Item model identifier is empty";
        }
        if (it == null) {
            error = error + " Item model type is empty";
        } else {
            itemModelEdited.setItemType(it);
        }
        if (itemModelEdited.getName().equals("")) {
            error = error + " Item model name is empty";
        }
        if (itemModelEdited.getDescription().equals("")) {
            error = error + " Item model description is empty";
        }

        if (file == null) {
            itemModelEdited.setImg((itemModelDao.getItemModel(itemModelEdited.getItemModelId())).getImg());
        } else {
            String filename = file.getOriginalFilename();
            String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
                String pathStr = ITEMS_IMAGES_DIR + itemModelEdited.getName().replace(" ", "_") + extension;
                new File(pathStr); // Create dest file to save
                Path path = Paths.get(pathStr);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                String imagePath = "images/createdItems/" + itemModelEdited.getName().replace(" ", "_") + extension;
                itemModelEdited.setImg(imagePath);
        }

        if (error.length() == 0) {
            itemModelDao.editItemModel(itemModelEdited);
            returnStr = "redirect:/index";
        } else {
            returnStr = "redirect:/itemModel/" + itemModelEdited.getItemModelId() + "/edit?error=" + error;
        }

        return returnStr;
    }
}
