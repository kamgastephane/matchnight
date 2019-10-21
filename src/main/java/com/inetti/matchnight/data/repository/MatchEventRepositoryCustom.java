package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.model.MatchEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;

public interface MatchEventRepositoryCustom {

    /**
     * Get a cached list of match events from the selected year and month
     * @param year the year
     * @param month the month as an integer
     * @param pageable the pageable
     * @return a list of matchEvents
     */
    @Cacheable(value = RepositoryConstants.MATCH_EVENT_CACHE_NAME, key = RepositoryConstants.EVENT_CACHE_KEY_MONTH)
    List<MatchEvent> findByMonthCached(Integer year, Integer month, Pageable pageable);

    /**
     * purge the cache list of match event for this month and year
     * @param year the year
     * @param month the month
     */
    @CacheEvict(value = RepositoryConstants.MATCH_EVENT_CACHE_NAME, key = RepositoryConstants.EVENT_CACHE_KEY_MONTH)
    void purge(Integer year, Integer month);


    /**
     * Insert match event if they do not exist and update them if they do
     * the cache is then invalidated
     * @param updates a map with key equals to the externalId and value to the update to apply
     */
    @CacheEvict(value = RepositoryConstants.MATCH_EVENT_CACHE_NAME, allEntries = true)
    void update(Map<String, Update> updates);

    /**
     * Delete a {@link MatchEvent} and invalidate the cache
     * @param externalIds the provider id of this match event
     * @return true if the operation had success, false otherwise
     */
    @CacheEvict(value = RepositoryConstants.MATCH_EVENT_CACHE_NAME, allEntries = true)
    boolean delete(List<String> externalIds);

}
