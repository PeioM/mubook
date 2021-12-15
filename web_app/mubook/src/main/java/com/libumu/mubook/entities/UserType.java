package com.libumu.mubook.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "user_type")
public class UserType {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "usertypeid")
    private Integer userTypeId;
    @Column(name = "description")
    private String description;

    public void setUserTypeId(Integer id) {
        this.userTypeId = id;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /*public List<User> getUsers() {
        return users;
    }*/
}
