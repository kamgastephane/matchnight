package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.dto.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository<T extends Event> extends MongoRepository<T, String> {


}
