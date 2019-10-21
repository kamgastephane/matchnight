package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.model.SupportRequest;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends MongoRepository<SupportRequest, ObjectId>, RequestRepositoryCustom {

    @Query(RepositoryConstants.REQUEST_BY_PROJECT_ID)
    Set<SupportRequest> findByProjectId(String id);

    @Query(RepositoryConstants.REQUEST_BY_ID)
    @Cacheable(value = RepositoryConstants.REQUEST_CACHE_NAME, key = RepositoryConstants.REQUEST_CACHE_KEY_ID)
    Optional<SupportRequest> findByIdCached(ObjectId id);



}
