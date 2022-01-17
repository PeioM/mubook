package com.libumu.mubook.api;

import com.libumu.mubook.dao.item.ItemDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.specification.SpecificationDao;
import com.libumu.mubook.dao.status.StatusDao;
import com.libumu.mubook.entities.*;
import com.libumu.mubook.entities.SpecificationList.SpecificationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(path="itemModel")
public class ItemModelController {
    public final String LOCAL_UPLOAD_DIR = "C:/IMAGENES_ITEM_MODEL_PRUEBA/";

    @Autowired
    ItemModelDao itemModelDao;
    @Autowired
    ItemTypeDao itemTypeDao;
    @Autowired
    StatusDao statusDao;
    @Autowired
    ItemDao itemDao;
    @Autowired
    SpecificationDao specificationDao;

    @GetMapping(path="/model")
    public ModelAndView getItemModel(Model model,
                                     @RequestParam("id") long itemModelId){
        ItemModel itemModel = itemModelDao.getItemModel(itemModelId);

        model.addAttribute("itemModel", itemModel);

        return new ModelAndView("item", new ModelMap(model));
    }

    @GetMapping(path="/add")
    public String addItemModel(Model model){
        ItemModel itemModel = new ItemModel();
        List<ItemType> itemTypeList = itemTypeDao.getAllItemTypes();
        List<Status> statusList = statusDao.getAllStatus();
        List<Specification> specificationList = specificationDao.getAllSpecifications();
        List<Item> itemList = itemDao.getItemByItemModelItemModelId(itemModel.getItemModelId());

        model.addAttribute("itemModel", itemModel);
        model.addAttribute("itemTypes", itemTypeList);
        model.addAttribute("status", statusList);
        model.addAttribute("items", itemList);

        return "editCreateItem";
    }

    @GetMapping(path="/edit")
    public String editItemModel(Model model,
                                @ModelAttribute ItemModel itemModel){
        List<ItemType> itemTypeList = itemTypeDao.getAllItemTypes();
        List<Status> statusList = statusDao.getAllStatus();
        List<Specification> specifications = specificationDao.getAllSpecifications();
        List<Item> itemList = itemDao.getItemByItemModelItemModelId(itemModel.getItemModelId());


        return "editCreateItem";
    }

    @PostMapping(path="/add")
    public String createItemModel(Model model,
                                  @ModelAttribute ItemModel itemModel,
                                  @ModelAttribute Item items[],
                                  @ModelAttribute SpecificationList specifications[],
                                  @RequestParam("itemModelImg") MultipartFile file){
        String error = "";
        if(itemModel.getItemType() == null){
            error = "Item type is left";
        }
        if(itemModel.getName().equals("")){
            error = error + " Item model name is empty";
        }
        if(itemModel.getDescription().equals("")){
            error = error + " Item model description is empty";
        }
        if(itemModel.getIdentifier().equals("")){
            error = error + " Item model identifier is empty";
        }
        if (file == null || file.isEmpty() || file.getOriginalFilename()==null || file.getOriginalFilename().equals("")) {
            error = error + " Please upload the item model image";
        }
        if(error.length() == 0){
            String filename = file.getOriginalFilename();
            String extension = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
            try {
                String pathStr = LOCAL_UPLOAD_DIR + itemModel.getName() + extension;
                new File(pathStr);  //Create dest file to save
                Path path = Paths.get(pathStr);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                itemModel.setImg(path.toString());
                //In case there is no error redirect to home
            } catch (IOException e) {
                error = error + " Error uploading file";
            }
        }

        if(error.length() == 0){
            String itemError = "";
            itemModelDao.addItemModel(itemModel);
            for(Item item : items){
                if(item.getSerialNum().equals("")){
                    itemError = itemError + " Item serial number is empty";
                }
                if(item.getStatus() == null){
                    itemError = itemError + " Item status is empty";
                }
                if(itemError.length() == 0){
                    item.setItemModel(itemModel);
                    itemDao.addItem(item);
                }
                itemError = "";
            }
            String specError = "";
            /*for(SpecificationList specification : specifications){
                if(specification.getValue().equals("")){
                    specError = specError + " Specification value is empty";
                }

                if(specError.length() == 0){
                    specification.setItemModel(itemModel);

                }
            }*/
        }

        model.addAttribute("items", itemModelDao.getAllItemModels());

        return "redirect:/searchItems";
    }

    @PostMapping(path="/edit")
    public String editItemModel(Model model,
                                @ModelAttribute ItemModel itemModelEdited,
                                @ModelAttribute List<Item> items,
                                @ModelAttribute List<Specification> specifications){
        String error = "";
        if(!itemModelEdited.getIdentifier().equals("")){
            if(itemModelDao.countItemModelByIdentifierAndItemModelIdNotLike(itemModelEdited.getIdentifier(), itemModelEdited.getItemModelId()) > 0){
                error = error + " Item model identifier already exists";
            }
        }else{
            error = error + " Item model identifier is empty";
        }
        if(itemModelEdited.getItemType() == null){
            error = error + " Item model type is empty";
        }
        if(itemModelEdited.getName().equals("")){
            error = error + " Item model name is empty";
        }
        if(itemModelEdited.getDescription().equals("")){
            error = error + " Item model description is empty";
        }

        if(error.length() == 0){
            String itemError = "";
            List<Item> previousItems = itemDao.getItemByItemModelItemModelId(itemModelEdited.getItemModelId());
            for(Item item : items.toArray(new Item[0])){
                if(!previousItems.contains(item)){
                    if(item.getSerialNum().equals("")){
                        itemError = itemError + " Item serial number is empty";
                    }
                    if(item.getStatus() == null){
                        itemError = itemError + " Item status is empty";
                    }
                    if(itemError.length() == 0){
                        item.setItemModel(itemModelEdited);
                        itemDao.addItem(item);
                    }
                }
                itemError = "";
            }
        }


        model.addAttribute("items", itemModelDao.getAllItemModels());

        return "redirect:/searchItems";
    }

    /*@PostMapping(path="/delete")
    public String deleteItemModel(Model model,
                                  @ModelAttribute ItemModel itemModel){
        List<Item> items = itemDao.getItemByItemModelItemModelId(itemModel.getItemModelId());


        return "redirect:/index";
    }*/
}
