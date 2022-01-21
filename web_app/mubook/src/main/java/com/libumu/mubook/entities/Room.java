package com.libumu.mubook.entities;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GenericGenerator(name="room" , strategy="increment")
    @GeneratedValue(generator="room")
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "description")
    private String description;

    public Room(){}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}
