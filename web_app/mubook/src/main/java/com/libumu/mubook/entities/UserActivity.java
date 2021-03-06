package com.libumu.mubook.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name= "user_activity")
public class UserActivity {
    @Id
    @GenericGenerator(name="userActivity" , strategy="increment")
    @GeneratedValue(generator="userActivity")
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
