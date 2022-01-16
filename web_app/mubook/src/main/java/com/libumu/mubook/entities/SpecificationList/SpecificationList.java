package com.libumu.mubook.entities.SpecificationList;

import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.Specification;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "specification_list")
public class SpecificationList  implements Serializable {

    @EmbeddedId
    private SpecificationListId id;

    @ManyToOne
    @MapsId("itemModel")
    @JoinColumn(name = "item_model_id")
    private ItemModel itemModel;

    @ManyToOne
    @MapsId("specification")
    @JoinColumn(name = "specification_id")
    private Specification specification;

    @Column(name = "value")
    private String value;

    public Specification getSpecification(){
        return this.specification;
    }

    public String getValue(){
        return this.value;
    }

    public void setItemModel(ItemModel itemModel){
        this.itemModel = itemModel;
    }
}