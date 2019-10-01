package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.dto.Event;
import com.inetti.matchnight.data.dto.MatchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class MatchEventRepositoryImpl implements MatchEventRepositoryCustom{

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEventRepositoryImpl.class);

    private final MongoTemplate template;

    @Autowired
    public MatchEventRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }


    @Override
    @Cacheable(value = MatchEventRepository.MATCH_EVENT_CACHE_NAME, key = MatchEventRepository.CACHE_KEY_DATE)
    public List<MatchEvent> findByDateCached(LocalDate date) {
        Instant start = date.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant end = ZonedDateTime.of(date, LocalTime.MAX, ZoneId.of("UTC")).toInstant();
        Criteria criteria = Criteria.where(Event.DATE).lt(end).gte(start);
        return template.find(new Query(criteria), MatchEvent.class);
    }

    @Override
    @CacheEvict(value = MatchEventRepository.MATCH_EVENT_CACHE_NAME, key = MatchEventRepository.CACHE_KEY_DATE)
    public void purgeByDate(LocalDate date) {
        LOGGER.debug("purge matchEvent: key: {}", date);
    }

}
