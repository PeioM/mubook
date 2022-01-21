package com.libumu.mubook.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "button")
public class Button {

    @Id
    @GenericGenerator(name="button" , strategy="increment")
    @GeneratedValue(generator="button")
    @Column(name = "button_id")
    private Integer buttonId;

    @Column(name = "description")
    private String description;

    public Button(String description, int buttonId) {
        this.description = description;
        this.buttonId = buttonId;
    }

    public Button() {

    }
}
