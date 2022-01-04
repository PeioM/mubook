package com.libumu.mubook.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name= "user_activity")
public class UserActivity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "user_activity_id")
    private Integer userActivityId;

    @Column(name = "description")
    private String description;

    public UserActivity() {
    }

    public UserActivity(int id, String desc){
        this.userActivityId = id;
        this.description = desc;
    }

    public Integer getUserActivityId() {
        return userActivityId;
    }

    public void setUserActivityId(Integer id) {
        this.userActivityId = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
