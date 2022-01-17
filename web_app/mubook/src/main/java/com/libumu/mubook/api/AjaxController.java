package com.libumu.mubook.api;

import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.entities.ItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ajax")
public class AjaxController {

    ItemTypeDao itemTypeDao;
    ItemModelDao itemModelDao;
    @Autowired
    public AjaxController(ItemTypeDao itemTypeDao, ItemModelDao itemModelDao) {
        this.itemTypeDao = itemTypeDao;
        this.itemModelDao = itemModelDao;
    }

    @GetMapping("/filter/{itemType}")
    public String filterItemModels(@PathVariable("itemType") String itemType, WebRequest request, Model model) {
        //Obtain all filters
        Map<String, String[]> parameters = request.getParameterMap();
        //Obtain all Item Models filtered by ItemType
        List<ItemModel> itemModels = itemModelDao.getItemModelsByType(Integer.parseInt(itemType));
        //Obtain list of all item models ids
        List<Long> itemModelIds = obtainItemModelIds(itemModels);
        //Apply all filters one by one
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            int key = Integer.parseInt(entry.getKey());
            List<ItemModel> itemModelsBySpec = new ArrayList<>();
            //Add to item model list all models that match the requirements
            for (String value : entry.getValue()) {
                List<ItemModel> updatedModels = itemModelDao.getItemModelsBySpecification(itemModelIds, key, value);
                itemModelsBySpec.addAll(updatedModels);
            }
            //update the final list and the id list
            itemModels = itemModelsBySpec;
            itemModelIds = obtainItemModelIds(itemModels);
        }
        model.addAttribute("itemModels", itemModels);

        return "search :: resultBlock";
    }

    private List<Long> obtainItemModelIds(List<ItemModel> list){
        return list.stream().map(ItemModel::getItemModelId).collect(Collectors.toList());
    }
}
