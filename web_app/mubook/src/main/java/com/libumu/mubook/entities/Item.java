package com.libumu.mubook.entities;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name= "item")
public class Item {

    @Id
    @GenericGenerator(name="item", strategy="increment")
    @GeneratedValue(generator="item")
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "serial_num")
    private String serialNum;

    @ManyToOne
    @JoinColumn(name = "item_model_id", nullable = false)
    private ItemModel itemModel;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    public Item(){}

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public ItemModel getItemModel() {
        return itemModel;
    }

    public void setItemModel(ItemModel itemModel) {
        this.itemModel = itemModel;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
