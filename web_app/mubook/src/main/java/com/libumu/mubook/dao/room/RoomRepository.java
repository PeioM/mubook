package com.libumu.mubook.dao.room;

import com.libumu.mubook.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
