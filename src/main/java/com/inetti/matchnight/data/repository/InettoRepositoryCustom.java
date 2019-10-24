package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.model.Inetto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public interface InettoRepositoryCustom {

    /**
     * @param pageable a pageable
     * @return a cached list of all users
     */
    @Cacheable(value = RepositoryConstants.INETTO_CACHE_NAME)
    List<Inetto> findInettiCached(Pageable pageable);


    /**
     * update a single inetto and invalidate the cache
     * @param id the id of the inetto
     * @param update the update to apply
     */
    @CacheEvict(value = RepositoryConstants.INETTO_CACHE_NAME, allEntries = true)
    boolean update(String id, Update update);

    /**
     * clean cache
     */
    @CacheEvict(value = RepositoryConstants.INETTO_CACHE_NAME, allEntries = true)
    void purgeCache();


    /**
     * save a single inetto and invalidagte the cache
     * @param inetto a single inetto to save
     * @return the created inetto
     */
    @CacheEvict(value = RepositoryConstants.INETTO_CACHE_NAME, allEntries = true)
    public Inetto saveAndInvalidate(Inetto inetto);


    /**
     * The number of inetti i have in the collection
     * @return a {@link Long} number
     */
    @Cacheable(value = RepositoryConstants.INETTO_CACHE_NAME, key = "count")
    public Long getInettiCount();
}
