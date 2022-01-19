package com.libumu.mubook.api;

import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.specification.SpecificationDao;
import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.ItemType;
import com.libumu.mubook.entities.Specification;
import com.libumu.mubook.entities.SpecificationList.SpecificationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/search")
public class SearchController {

    ItemTypeDao itemTypeDao;
    ItemModelDao itemModelDao;
    SpecificationDao specificationDao;
    @Autowired
    public SearchController(ItemTypeDao itemTypeDao, ItemModelDao itemModelDao,SpecificationDao specificationDao) {
        this.itemTypeDao = itemTypeDao;
        this.itemModelDao = itemModelDao;
        this.specificationDao = specificationDao;
    }

    @GetMapping("")
    public String searchPage(Model model){
        model.addAttribute("itemTypes", itemTypeDao.getAllItemTypes());
        model.addAttribute("itemModels", itemModelDao.getAllItemModels());
        return "searchItems";
    }

    @GetMapping("/{itemType}")
    public String searchPage(@PathVariable("itemType") String itemTypeDesc,
                             Model model){

        //ItemModels filtered by ItemType
        ItemType itemType = itemTypeDao.getItemTypeByDesc(itemTypeDesc);
        List<ItemModel> itemModels = itemModelDao.getItemModelsByType(itemType.getItemTypeId());

        //Specifications
        Map<Specification, List<String>> specifications = new TreeMap<>();
        itemModels.forEach(
                im -> im.getSpecificationLists().forEach(
                        sl -> loadSpecifications(sl, specifications)));

        //Calculate pages to visualize
        List<Integer> keys = specificationDao.getAllSpecificationIds();
        List<String> values = getAllSpecificationValues(specifications);
        int totalItemModels = itemModelDao.getTotalItemModelByType(itemType.getItemTypeId());
        int pages = totalItemModels/AjaxController.ITEMS_PER_PAGE + 1;

        //Save in model
        model.addAttribute("actualItemType", itemType);
        model.addAttribute("itemModels", itemModels);
        model.addAttribute("specifications", specifications);
        model.addAttribute("pages", pages);

        return "search";
    }

    private List<String> getAllSpecificationValues(Map<Specification, List<String>> specifications) {
        List<String> values = new ArrayList<>();
        for (Map.Entry<Specification, List<String>> entry : specifications.entrySet()){
            values.addAll(entry.getValue());
        }
        return values;
    }

    private void loadSpecifications(SpecificationList sl, Map<Specification, List<String>> specifications){
        if(specifications.containsKey(sl.getSpecification())){
            String valueToAdd = sl.getValue();
            List<String> mapValues = specifications.get(sl.getSpecification());

            if(!mapValues.contains(valueToAdd)) {
                specifications.get(sl.getSpecification()).add(valueToAdd);
            }
        }
        else{
            List<String> values = new ArrayList<>();
            values.add(sl.getValue());
            specifications.put(sl.getSpecification(), values);
        }

    }
}
