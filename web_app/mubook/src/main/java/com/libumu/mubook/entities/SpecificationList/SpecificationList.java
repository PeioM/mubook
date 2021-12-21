package com.libumu.mubook.entities.SpecificationList;

import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.Specification;

import javax.persistence.*;

@Entity
@Table(name = "specification_list")
@IdClass(SpecificationListId.class)
public class SpecificationList {

    @Id
    @ManyToOne
    @JoinColumn(name = "item_model_id", referencedColumnName = "item_model_id")
    private ItemModel itemModel;

    @Id
    @ManyToOne
    @JoinColumn(name = "specification_id", referencedColumnName = "specification_id")
    private Specification specification;

    @Column(name = "value")
    private String value;
}