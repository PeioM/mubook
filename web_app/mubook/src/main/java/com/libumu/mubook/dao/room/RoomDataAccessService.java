package com.libumu.mubook.dao.room;

import com.libumu.mubook.entities.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomDataAccessService implements RoomDao {

    @Autowired
    private RoomRepository repository;

    @Override
    public List<Room> getAllRooms() {
        return (List<Room>) repository.findAll();
    }

    @Override
    public Room getRoom(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editRoom(Room room) {
        repository.save(room);
    }

    @Override
    public void deleteRoom(long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteRoom(Room room) {
        repository.delete(room);
    }

    @Override
    public void addRoom(Room room) {
        repository.save(room);
    }
}
