package com.libumu.mubook.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "item_type")
public class ItemType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_type_id")
    private Integer itemTypeId;

    @Column(name = "description")
    private String description;

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
