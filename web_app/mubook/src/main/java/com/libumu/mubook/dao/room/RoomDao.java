package com.libumu.mubook.dao.room;

import com.libumu.mubook.entities.Room;

import java.util.List;

public interface RoomDao {

    public List<Room> getAllRooms();
    public Room getRoom(long id);
    public void editRoom(Room room);
    public void deleteRoom(long id);
    public void deleteRoom(Room room);
    public void addRoom(Room room);

}
