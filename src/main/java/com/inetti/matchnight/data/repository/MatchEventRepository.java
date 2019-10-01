package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.dto.MatchEvent;

public interface MatchEventRepository extends EventRepository<MatchEvent>, MatchEventRepositoryCustom {

    static final String EVENT_BY_ID = "{ 'externalId' : ?0 }";
    static final String MATCH_EVENT_CACHE_NAME = "match_event_cache";
    static final String CACHE_KEY_DATE = "#date.toString()";

//
//    @Query(EVENT_BY_ID)
//    @Cacheable(value = MATCH_EVENT_CACHE_NAME)
//    Optional<MatchEvent> findByExternalIdCached(String id);



}
