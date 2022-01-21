package com.libumu.mubook.api;

import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.specification.SpecificationDao;
import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.ItemType;
import com.libumu.mubook.entities.Specification;
import com.libumu.mubook.entities.SpecificationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

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

        ItemType itemType = itemTypeDao.getItemTypeByDesc(itemTypeDesc);

        //Specifications
        Map<Specification, List<String>> specifications = new TreeMap<>();
        List<Object[]> specificationValues = specificationDao.getAllSpecificationAndValuesByItemType(itemType.getItemTypeId());
        specificationValues.forEach(
                row -> saveInMap(specifications, row)
        );

        //Save in model
        model.addAttribute("actualItemType", itemType);
        model.addAttribute("specifications", specifications);

        return "searchItems";
    }

    private void saveInMap(Map<Specification, List<String>> map, Object[] row) {
        Integer keyId = (Integer) row[0];
        Specification key = specificationDao.getSpecification(keyId);
        String value = (String) row[1];

        if(map.containsKey(key)){
            map.get(key).add(value);
        }
        else{
            List<String> values = new ArrayList<>();
            values.add(value);
            map.put(key, values);
        }
    }
}
