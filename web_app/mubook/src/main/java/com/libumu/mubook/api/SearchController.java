package com.libumu.mubook.api;

import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.ItemType;
import com.libumu.mubook.entities.Specification;
import com.libumu.mubook.entities.SpecificationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/search")
public class SearchController {

    ItemTypeDao itemTypeDao;
    ItemModelDao itemModelDao;
    @Autowired
    public SearchController(ItemTypeDao itemTypeDao, ItemModelDao itemModelDao) {
        this.itemTypeDao = itemTypeDao;
        this.itemModelDao = itemModelDao;
    }

    @GetMapping("")
    public String searchPage(Model model){
        model.addAttribute("itemTypes", itemTypeDao.getAllItemTypes());
        model.addAttribute("itemModels", itemModelDao.getAllItemModels());
        return "searchItems";
    }

    @GetMapping("/*")
    public String searchPage(Model model, HttpServletRequest request){

        //ItemModels filtered by ItemType
        String url = request.getRequestURL().toString();
        String itemTypeDesc = url.substring(url.lastIndexOf("/")+1);
        ItemType itemType = itemTypeDao.getItemTypeByDesc(itemTypeDesc);
        List<ItemModel> itemModels = itemModelDao.getItemModelsByType(itemType.getItemTypeId());

        //Specifications
        Map<Specification, List<String>> specifications = new TreeMap<>();
        itemModels.forEach(
                im -> im.getSpecificationLists().forEach(
                        sl -> loadSpecifications(sl, specifications)));

        //Save in model
        model.addAttribute("actualItemType", itemType);
        model.addAttribute("itemModels", itemModels);
        model.addAttribute("specifications", specifications);

        return "searchItems";
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
