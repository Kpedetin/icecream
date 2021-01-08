package com.blackbox.dao.repository;

import com.blackbox.dao.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {


}
