package com.libumu.mubook.api;

import com.google.gson.Gson;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.specification.SpecificationDao;
import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.ItemType;
import com.libumu.mubook.entitiesAsClasses.ItemModelClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ajax")
public class AjaxController {

    public final static int ITEMS_PER_PAGE = 2;

    private Map<String, String[]> itemModelFilters;

    ItemTypeDao itemTypeDao;
    ItemModelDao itemModelDao;
    SpecificationDao specificationDao;
    @Autowired
    public AjaxController(ItemTypeDao itemTypeDao, ItemModelDao itemModelDao, SpecificationDao specificationDao) {
        this.itemTypeDao = itemTypeDao;
        this.itemModelDao = itemModelDao;
        this.specificationDao = specificationDao;
        this.itemModelFilters = new TreeMap<>();
    }

    @GetMapping("/filterItemModels/{itemType}/{page}")
    @ResponseBody
    public String filterItemModels(@PathVariable("itemType") String itemType,
                                   @PathVariable("page") String pageStr,
                                   WebRequest request) {
        //Parse page
        int page = Integer.parseInt(pageStr);
        //Obtain all filters by cleaning parameters (when it is array key = ...[])
        itemModelFilters = clearKeys(request.getParameterMap());
        //Obtain all Item Models filtered by ItemType
        List<ItemModel> itemModels;
        if(itemModelFilters.isEmpty()) {
            itemModels = itemModelDao.getAllItemModelsByTypeAndBetween(
                    itemTypeDao.getItemTypeByDesc(itemType).getItemTypeId(), page);
        }
        else{
            List<Integer> keys = parseEntireSet(itemModelFilters.keySet());
            List<String> values = getAllValues(itemModelFilters);

            itemModels = itemModelDao.getItemModelsBySpecificationRowsBetween(keys, values, page);
        }
        List<ItemModelClass> itemModelsToSend = new ArrayList<>();
        for(ItemModel im : itemModels) itemModelsToSend.add(new ItemModelClass(im));

        Gson gson = new Gson();
        return gson.toJson(itemModelsToSend);
    }

    @GetMapping("/filterItemModelsGetPages/{itemType}")
    @ResponseBody
    public String updateItemModelPages(@PathVariable("itemType") String itemTypeDesc){
        ItemType itemType = itemTypeDao.getItemTypeByDesc(itemTypeDesc);
        List<Integer> keys;
        List<String> values;
        int totalItemModels;
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
}
