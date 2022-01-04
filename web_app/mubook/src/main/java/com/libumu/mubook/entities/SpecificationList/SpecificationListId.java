package com.libumu.mubook.entities.SpecificationList;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SpecificationListId implements Serializable{
    @Column(name = "item_model_id")
    private Long itemModel;
    @Column(name = "specification_id")
    private Integer specification;

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
        return specification.hashCode() + itemModel.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        SpecificationListId sl = (SpecificationListId) obj;
        return (sl.getSpecification()==this.specification && sl.getItemModel()==this.itemModel);
    }
}
