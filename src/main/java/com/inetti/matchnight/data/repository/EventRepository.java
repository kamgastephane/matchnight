package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository<T extends Event> extends MongoRepository<T, String> {


}
