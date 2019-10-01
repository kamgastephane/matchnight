package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.dto.SupportRequest;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends MongoRepository<SupportRequest, ObjectId>, RequestRepositoryCustom {

    static final String REQUEST_BY_ID = "{ '_id' : ?0 }";
    static final String REQUEST_BY_PROJECT_ID = "{ 'projectId' : ?0, 'archived' : false }";
    static final String REQUEST_CACHE_NAME = "request_cache";

    static final String CACHE_KEY_ID = "#id.toString()";

    @Query(REQUEST_BY_PROJECT_ID)
    Set<SupportRequest> findByProjectId(String id);

//    @Cacheable(value = REQUEST_CACHE_NAME, key = "'by_project_id_'.concat(#id)")
//    @Query(REQUEST_BY_PROJECT_ID)
//    Set<Request> findByProjectIdCached(String id);

    @Query(REQUEST_BY_ID)
    @Cacheable(value = REQUEST_CACHE_NAME, key = CACHE_KEY_ID)
    Optional<SupportRequest> findByIdCached(ObjectId id);



}
