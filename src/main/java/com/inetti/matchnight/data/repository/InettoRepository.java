package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.model.Inetto;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface InettoRepository extends MongoRepository<Inetto, ObjectId>, InettoRepositoryCustom{

    /**
     * find an inetto
     * the result is cached
     * @param username the username
     * @return a single inetto or {@link Optional#empty()}
     */
    @Query(RepositoryConstants.INETTO_BY_USERNAME)
    @Cacheable(value = RepositoryConstants.INETTO_CACHE_NAME)
    Optional<Inetto> findByUsernameCached(String username);




}
