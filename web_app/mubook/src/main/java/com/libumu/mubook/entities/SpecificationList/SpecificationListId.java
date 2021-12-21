package com.libumu.mubook.entities.SpecificationList;

import java.io.Serializable;

public class SpecificationListId implements Serializable{
    private long itemModel;
    private int specification;

    public SpecificationListId() {
    }

    public long getItemModel() {
        return itemModel;
    }

    public void setItemModel(long itemModel) {
        this.itemModel = itemModel;
    }

    public int getSpecification() {
        return specification;
    }

    public void setSpecification(int specification) {
        this.specification = specification;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        SpecificationListId sl = (SpecificationListId) obj;
        return (sl.getSpecification()==this.specification && sl.getItemModel()==this.itemModel);
    }
}
