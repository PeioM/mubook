package com.libumu.mubook.entities;

import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.Specification;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "specification_list")
public class SpecificationList  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "specification_list_id")
    private Long specificationListId;

    @ManyToOne
    @JoinColumn(name = "item_model_id")
    private ItemModel itemModel;

    @ManyToOne
    @JoinColumn(name = "specification_id")
    private Specification specification;

    @Column(name = "value")
    private String value;

    public Specification getSpecification(){
        return this.specification;
    }

    public void setSpecification(Specification specification){
        this.specification = specification;
    }

    public String getValue(){
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setItemModel(ItemModel itemModel){
        this.itemModel = itemModel;
    }

    public ItemModel getItemModel(){
        return this.itemModel;
    }

    public void setSpecificationListId(Long specificationListId){
        this.specificationListId = specificationListId;
    }

    public Long getSpecificationListId() {
        return this.specificationListId;
    }
}