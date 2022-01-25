package com.libumu.mubook.entities;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "status")
public class Status {

    @Id
    @GenericGenerator(name="status" , strategy="increment")
    @GeneratedValue(generator="status")
    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "description")
    private String description;

    public Status(){
        //This is empty
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
