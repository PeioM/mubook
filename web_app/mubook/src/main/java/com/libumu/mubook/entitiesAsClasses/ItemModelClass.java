package com.libumu.mubook.entitiesAsClasses;


import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.ItemType;
import com.libumu.mubook.entities.Specification;
import com.libumu.mubook.entities.SpecificationList;

import java.util.ArrayList;
import java.util.List;

public class ItemModelClass {

    private Long itemModelId;
    private String description;
    private String name;
    private String identifier;
    private String img;

    private ItemTypeClass itemType;
    private List<SpecificationListClass> specificationLists;

    public ItemModelClass(ItemModel itemModel){
        this.itemModelId = itemModel.getItemModelId();
        this.description = itemModel.getDescription();
        this.name = itemModel.getName();
        this.identifier = itemModel.getIdentifier();
        this.img = itemModel.getImg();
        this.itemType = new ItemTypeClass(itemModel.getItemType());
        this.specificationLists = SpecificationListClass.loadNewList(itemModel.getSpecificationLists());
    }

    public Long getItemModelId() {
        return itemModelId;
    }

    public void setItemModelId(Long itemModelId) {
        this.itemModelId = itemModelId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public ItemTypeClass getItemType() {
        return itemType;
    }

    public void setItemType(ItemTypeClass itemType) {
        this.itemType = itemType;
    }

    public List<SpecificationListClass> getSpecificationLists() {
        return specificationLists;
    }

    public void setSpecificationLists(List<SpecificationListClass> specificationLists) {
        this.specificationLists = specificationLists;
    }

    //CLASSES FOR ITEM MODEL
    public static class ItemTypeClass {
        private Integer itemTypeId;
        private String description;

        public ItemTypeClass(ItemType itemType) {
            this.itemTypeId = itemType.getItemTypeId();
            this.description = itemType.getDescription();
        }

        public Integer getItemTypeId() {
            return itemTypeId;
        }

        public void setItemTypeId(Integer itemTypeId) {
            this.itemTypeId = itemTypeId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class SpecificationListClass {

        private SpecificationClass specification;
        private String value;

        public SpecificationListClass(SpecificationList sl) {
            this.specification = new SpecificationClass(sl.getSpecification());
            this.value = sl.getValue();
        }

        public SpecificationClass getSpecification(){
            return this.specification;
        }

        public String getValue(){
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setSpecification(SpecificationClass specification) {
            this.specification = specification;
        }

        public static List<SpecificationListClass> loadNewList(List<SpecificationList> specificationLists) {
            List<SpecificationListClass> result = new ArrayList<>();

            for(SpecificationList sl : specificationLists){
                SpecificationListClass slc = new SpecificationListClass(sl);
                result.add(slc);
            }
            return result;
        }
    }

    public static class SpecificationClass {
        private Integer specificationId;
        private String description;

        public SpecificationClass(Specification specification) {
            this.specificationId = specification.getSpecificationId();
            this.description = specification.getDescription();
        }

        public String getDescription() {
            return description;
        }

        public Integer getSpecificationId() {
            return specificationId;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setSpecificationId(Integer specificationId) {
            this.specificationId = specificationId;
        }
    }
}
