package com.libumu.mubook.entities.Opinion;

import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "opinion")
public class Opinion implements Serializable {

    @EmbeddedId
    private OpinionId id;

    @ManyToOne
    @MapsId("user")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("itemModel")
    @JoinColumn(name = "item_model_id")
    private ItemModel itemModel;

    @Column(name = "rate")
    private int rate;
    @Column(name = "description")
    private String description;
    @Column(name = "date")
    private Date date;
}