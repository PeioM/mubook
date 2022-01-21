package com.libumu.mubook.entities;


import com.libumu.mubook.entities.SpecificationList.SpecificationList;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name= "item_model")
public class ItemModel {

    @Id
    @GenericGenerator(name="itemModel" , strategy="increment")
    @GeneratedValue(generator="itemModel")
    @Column(name = "item_model_id")
    private Long itemModelId;

    @Column(name = "description")
    private String description;
    @Column(name = "name")
    private String name;
    @Column(name = "identifier")
    private String identifier;
    @Column(name = "img")
    private String img;

    @ManyToOne
    @JoinColumn(name = "item_type_id", nullable = false)
    private ItemType itemType;

    @OneToMany(mappedBy = "itemModel")
    List<SpecificationList> specificationLists;

    public ItemModel(){}

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

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public List<SpecificationList> getSpecificationLists() {
        return specificationLists;
    }

    public void setSpecificationLists(List<SpecificationList> specificationLists) {
        this.specificationLists = specificationLists;
    }
}
