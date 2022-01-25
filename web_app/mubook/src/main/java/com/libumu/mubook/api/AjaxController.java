package com.libumu.mubook.api;

import com.google.gson.Gson;
import com.libumu.mubook.dao.button.ButtonDao;
import com.libumu.mubook.dao.buttonClick.ButtonClickDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.reservation.ReservationDao;
import com.libumu.mubook.dao.specification.SpecificationDao;
import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.*;
import com.libumu.mubook.entitiesAsClasses.ItemModelClass;
import com.libumu.mubook.entitiesAsClasses.ReservationClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/ajax")
public class AjaxController {

    public final static int ITEMS_PER_PAGE = 9;

    private Map<String, String[]> itemModelFilters;

    ItemTypeDao itemTypeDao;
    ItemModelDao itemModelDao;
    SpecificationDao specificationDao;
    UserDao userDao;
    ButtonClickDao buttonClickDao;
    ButtonDao buttonDao;
    ReservationDao reservationDao;
    @Autowired
    public AjaxController(ItemTypeDao itemTypeDao, ItemModelDao itemModelDao,
                          SpecificationDao specificationDao, UserDao userDao,
                          ButtonClickDao buttonClickDao, ButtonDao buttonDao,
                          ReservationDao reservationDao) {
        this.itemTypeDao = itemTypeDao;
        this.itemModelDao = itemModelDao;
        this.specificationDao = specificationDao;
        this.itemModelFilters = new TreeMap<>();
        this.userDao = userDao;
        this.buttonClickDao = buttonClickDao;
        this.buttonDao = buttonDao;
        this.reservationDao = reservationDao;
    }

    //Item Model filters
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

    //User filters
    @GetMapping("/filterUsers/{userType}/{page}")
    @ResponseBody
    public String filterUsers(@PathVariable("userType") String userType,
                              @PathVariable("page") String pageStr,
                              WebRequest request){
        int page = Integer.parseInt(pageStr);
        List<User> users;
        String containStr = request.getParameter("containStr");
        if(userType.equals("-")){
            users = userDao.getUsersBetweenContaining(page, containStr);
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
        if(userType.equals("-")){
            totalUsers = userDao.getuserCountContaining(containStr);
        }
        else{
            totalUsers = userDao.getuserCountByTypeAndContaining(userType, containStr);
        }
        double pages = (double) totalUsers/AjaxController.ITEMS_PER_PAGE;

        return String.valueOf(pages);
    }

    //Reservation filters
    @GetMapping("/filterReservations/{itemModel}/{page}")
    @ResponseBody
    public String filterReservations(@PathVariable("itemModel") String itemModel,
                                     @PathVariable("page") String pageStr,
                                     WebRequest request){
        int page = Integer.parseInt(pageStr);
        List<Reservation> reservations;
        boolean active = Boolean.parseBoolean(request.getParameter("active"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = userDao.getUserByUsername(authentication.getName());
        long loggedUserId = userDao.getUserByUsername(authentication.getName()).getUserId();
        //If its admin
        if(loggedUser.getUserType().getUserTypeId().equals("ADMIN") || loggedUser.getUserType().getUserTypeId().equals("WORKER")){
            if(itemModel.equals("-")){
                if(active){
                    reservations = reservationDao.getActiveReservationsBetween(page);
                }
                else{
                    reservations = reservationDao.getReservationsBetween(page);
                }
            }
            else if(itemModel.contains("itemType-")){
                String itemType = itemModel.replace("itemType-","");
                if(active){
                    reservations = reservationDao.getActiveReservationsByItemTypeBetween(
                            Integer.parseInt(itemType), page);
                }
                else{
                    reservations = reservationDao.getReservationsByItemTypeBetween(
                            Integer.parseInt(itemType), page);
                }
            }
            else{
                if(active){
                    reservations = reservationDao.getActiveReservationsByItemModelBetween(
                            Long.parseLong(itemModel), page);
                }
                else{
                    reservations = reservationDao.getReservationsByItemModelBetween(
                            Long.parseLong(itemModel), page);
                }
            }
        }
        else{
            if(itemModel.equals("-")){
                if(active){
                    reservations = reservationDao.getActiveReservationsBetweenForUser(page, loggedUserId);
                }
                else{
                    reservations = reservationDao.getReservationsBetweenForUser(page, loggedUserId);
                }
            }
            else if(itemModel.contains("itemType-")){
                String itemType = itemModel.replace("itemType-","");
                if(active){
                    reservations = reservationDao.getActiveReservationsByItemTypeBetweenForUser(
                            Integer.parseInt(itemType), page, loggedUserId);
                }
                else{
                    reservations = reservationDao.getReservationsByItemTypeBetweenForUser(
                            Integer.parseInt(itemType), page, loggedUserId);
                }
            }
            else{
                if(active){
                    reservations = reservationDao.getActiveReservationsByItemModelBetweenForUser(
                            Long.parseLong(itemModel), page, loggedUserId);
                }
                else{
                    reservations = reservationDao.getReservationsByItemModelBetweenForUser(
                            Long.parseLong(itemModel), page, loggedUserId);
                }
            }
        }
        List<ReservationClass> resultList = new ArrayList<>();
        for(Reservation res : reservations){
            resultList.add(new ReservationClass(res));
        }

        Gson gson = new Gson();
        return gson.toJson(resultList);
    }

    @GetMapping("/filterReservationsGetPages/{itemModel}")
    @ResponseBody
    public String filterReservationsGetPage(@PathVariable("itemModel") String itemModel,
                                            WebRequest request){
        int totalReservations;
        boolean active = Boolean.parseBoolean(request.getParameter("active"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = userDao.getUserByUsername(authentication.getName());
        long loggedUserId = userDao.getUserByUsername(authentication.getName()).getUserId();
        //If its admin
        if(loggedUser.getUserType().getUserTypeId().equals("ADMIN") || loggedUser.getUserType().getUserTypeId().equals("WORKER")){
            if(active) {
                if (itemModel.equals("-")){
                    totalReservations = reservationDao.getTotalActiveReservationCount();
                }
                else if(itemModel.contains("itemType-")){
                    String itemType = itemModel.replace("itemType-","");
                    totalReservations = reservationDao.getActiveReservationCountByItemType(
                            Integer.parseInt(itemType));
                }
                else{
                    totalReservations = reservationDao.getActiveReservationCountByItemModel(
                            Long.parseLong(itemModel));
                }
            }
            else{
                if (itemModel.equals("-")){
                    totalReservations = reservationDao.getTotalReservationCount();
                }
                else if(itemModel.contains("itemType-")){
                    String itemType = itemModel.replace("itemType-","");
                    totalReservations = reservationDao.getReservationCountByItemType(
                            Integer.parseInt(itemType));
                }
                else{
                    totalReservations = reservationDao.getReservationCountByItemModel(
                            Long.parseLong(itemModel));
                }
            }
        }
        else{
            if(active) {
                if (itemModel.equals("-")){
                    totalReservations = reservationDao.getTotalActiveReservationCountForUser(loggedUserId);
                }
                else if(itemModel.contains("itemType-")){
                    String itemType = itemModel.replace("itemType-","");
                    totalReservations = reservationDao.getActiveReservationCountByItemTypeForUser(
                            Integer.parseInt(itemType),loggedUserId);
                }
                else{
                    totalReservations = reservationDao.getActiveReservationCountByItemModelForUser(
                            Long.parseLong(itemModel),loggedUserId);
                }
            }
            else{
                if (itemModel.equals("-")){
                    totalReservations = reservationDao.getTotalReservationCountForUser(loggedUserId);
                }
                else if(itemModel.contains("itemType-")){
                    String itemType = itemModel.replace("itemType-","");
                    totalReservations = reservationDao.getReservationCountByItemTypeForUser(
                            Integer.parseInt(itemType),loggedUserId);
                }
                else{
                    totalReservations = reservationDao.getReservationCountByItemModelForUser(
                            Long.parseLong(itemModel),loggedUserId);
                }
            }
        }

        double pages = (double) totalReservations/AjaxController.ITEMS_PER_PAGE;

        return String.valueOf(pages);
    }

    //Grafana inserts
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
