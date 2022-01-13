package com.libumu.mubook.api;

import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "search";
    }
}
