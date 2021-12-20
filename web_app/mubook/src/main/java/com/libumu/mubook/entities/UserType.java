package com.libumu.mubook.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name= "user_type")
public class UserType {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "user_type_id")
    private String userTypeId;
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "userType")
    private List<User> users;

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

    public List<User> getUsers() {
        return users;
    }
}
