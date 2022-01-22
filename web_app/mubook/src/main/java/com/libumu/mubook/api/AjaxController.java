package com.libumu.mubook.api;

import com.google.gson.Gson;
import com.libumu.mubook.dao.button.ButtonDao;
import com.libumu.mubook.dao.buttonClick.ButtonClickDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.specification.SpecificationDao;
import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.*;
import com.libumu.mubook.entitiesAsClasses.ItemModelClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ajax")
public class AjaxController {

    public final static int ITEMS_PER_PAGE = 6;

    private Map<String, String[]> itemModelFilters;

    ItemTypeDao itemTypeDao;
    ItemModelDao itemModelDao;
    SpecificationDao specificationDao;
    UserDao userDao;
    ButtonClickDao buttonClickDao;
    ButtonDao buttonDao;
    @Autowired
    public AjaxController(ItemTypeDao itemTypeDao, ItemModelDao itemModelDao,
                          SpecificationDao specificationDao, UserDao userDao,
                          ButtonClickDao buttonClickDao, ButtonDao buttonDao) {
        this.itemTypeDao = itemTypeDao;
        this.itemModelDao = itemModelDao;
        this.specificationDao = specificationDao;
        this.itemModelFilters = new TreeMap<>();
        this.userDao = userDao;
        this.buttonClickDao = buttonClickDao;
        this.buttonDao = buttonDao;
    }

    @GetMapping("/filterItemModels/{itemType}/{page}")
    @ResponseBody
    public String filterItemModels(@PathVariable("itemType") String itemType,
                                   @PathVariable("page") String pageStr,
                                   WebRequest request) {
        //Parse page
        int page = Integer.parseInt(pageStr);
        List<ItemModel> itemModels;

        if(itemType.equals("all")){
            itemModels = itemModelDao.getAllItemModelsBetween(page);
        }
        else{
            //Obtain all filters by cleaning parameters (when it is array key = ...[])
            itemModelFilters = clearKeys(request.getParameterMap());
            //Obtain all Item Models filtered by ItemType

            if(itemModelFilters.isEmpty()) {
                itemModels = itemModelDao.getAllItemModelsByTypeAndBetween(
                        itemTypeDao.getItemTypeByDesc(itemType).getItemTypeId(), page);
            }
            else{
                List<Integer> keys = parseEntireSet(itemModelFilters.keySet());
                List<String> values = getAllValues(itemModelFilters);

                itemModels = itemModelDao.getItemModelsBySpecificationRowsBetween(keys, values, page);
            }
        }
        //Convert from entity to class to be able to access from thymeleaf
        List<ItemModelClass> itemModelsToSend = new ArrayList<>();
        for(ItemModel im : itemModels) itemModelsToSend.add(new ItemModelClass(im));

        Gson gson = new Gson();
        return gson.toJson(itemModelsToSend);
    }

    @GetMapping("/filterItemModelsGetPages/{itemType}")
    @ResponseBody
    public String updateItemModelPages(@PathVariable("itemType") String itemTypeDesc){
        int totalItemModels;

        if(itemTypeDesc.equals("all")){
            totalItemModels = itemModelDao.getTotalItemModels();
        }
        else{
            ItemType itemType = itemTypeDao.getItemTypeByDesc(itemTypeDesc);
            List<Integer> keys;
            List<String> values;

            //If no filter get all ItemModels count by type
            if(itemModelFilters.isEmpty()) {
                totalItemModels = itemModelDao.getTotalItemModelByType(itemType.getItemTypeId());
            }
            //Else, get count of item model by type and filter
            else {
                keys = parseEntireSet(itemModelFilters.keySet());
                values = getAllValues(itemModelFilters);
                totalItemModels = itemModelDao.getTotalItemModelFiltered(keys, values,itemType.getItemTypeId());
            }
        }
        double pages = (double) totalItemModels/AjaxController.ITEMS_PER_PAGE;

        return String.valueOf(pages);
    }

    private Map<String, String[]> clearKeys(Map<String, String[]> parameters) {
        Map<String, String[]> result = new TreeMap<>();
        for (Map.Entry<String, String[]> entry : parameters.entrySet()){
            String clearKey = entry.getKey().substring(0, entry.getKey().length()-2);
            result.put(clearKey, entry.getValue());
        }
        return result;
    }

    public static List<Integer> parseEntireSet(Set<String> set) {
        List<String> listStr = new ArrayList<>(set);
        List<Integer> result = new ArrayList<>();
        for(String value : listStr){
            result.add(Integer.parseInt(value));
        }
        return result;
    }

    public static List<String> getAllValues(Map<String, String[]> map) {
        List<String> result = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            List<String> valueList =  Arrays.asList(entry.getValue());
            result.addAll(valueList);
        }

        return result;
    }

    public static List<Long> obtainItemModelIds(List<ItemModel> list){
        return list.stream().map(ItemModel::getItemModelId).collect(Collectors.toList());
    }

    @GetMapping("/filterUsers/{userType}/{page}")
    @ResponseBody
    public String filterUsers(@PathVariable("userType") String userType,
                              @PathVariable("page") String pageStr,
                              WebRequest request){
        int page = Integer.parseInt(pageStr);
        List<User> users;
        String containStr = request.getParameter("containStr");
        if(userType.equals(" - ")){
            users = userDao.getUsersBetween(page);
        }
        else{
            users = userDao.getUsersByTypeAndBetweenAndContainig(userType, page, containStr);
        }

        Gson gson = new Gson();
        return gson.toJson(users);
    }


    @GetMapping("/filterUsersGetPages/{userType}")
    @ResponseBody
    public String filterUsersGetPage(@PathVariable("userType") String userType,
                              WebRequest request){
        int totalUsers;
        String containStr = request.getParameter("containStr");
        if(userType.equals(" - ")){
            totalUsers = userDao.getuserCountContaining(containStr);
        }
        else{
            totalUsers = userDao.getuserCountByTypeAndContaining(userType, containStr);
        }
        double pages = (double) totalUsers/AjaxController.ITEMS_PER_PAGE;

        Gson gson = new Gson();
        return String.valueOf(pages);
    }

    @GetMapping("/registerGrafana/{buttonId}")
    @ResponseBody
    public String registryButtonClickGrafana(@PathVariable("buttonId") String buttonId,
                                             Principal principal){
        String result = "";
        String username;
        if(principal != null) username = principal.getName();
        else username = "anonymousUser";

        User user = userDao.getUserByUsername(username);
        Button button = buttonDao.getButton(Integer.parseInt(buttonId));
        ButtonClick buttonClick = new ButtonClick(button, user);
        buttonClickDao.addButtonClick(buttonClick);
        result = "success";

        return result;
    }
}
