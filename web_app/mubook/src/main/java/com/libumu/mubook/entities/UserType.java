package com.libumu.mubook.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name= "user_type")
public class UserType {

    @Id
    @GenericGenerator(name="userType" , strategy="increment")
    @GeneratedValue(generator="userType")
    @Column(name = "user_type_id")
    private String userTypeId;
    @Column(name = "description")
    private String description;

    public void setUserTypeId(String id) {
        this.userTypeId = id;
    }

    public String getUserTypeId() {
        return userTypeId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
