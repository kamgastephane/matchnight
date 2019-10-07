package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.dto.Event;
import com.inetti.matchnight.data.dto.MatchEvent;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * Insert match event if they do not exist and update them if they do
     * @param updates a map with key equals to the externalId and value to the update to apply
     */
    @Override
    public void update(Map<String, Update> updates) {

        final BulkOperations operations = template.bulkOps(BulkOperations.BulkMode.ORDERED, MatchEvent.class);
        updates.forEach((externalId, update) -> {
            final Criteria criteria = Criteria.where(Event.EXTERNAL_ID).is(externalId).and(Event.TYPE).is(Event.Type.FOOTBALL);
            operations.upsert(new Query(criteria), update);
        });
        operations.execute();
    }

    @Override
    public boolean delete(List<String> externalIds) {
        final Criteria criteria = Criteria.where(Event.TYPE).is(Event.Type.FOOTBALL).and(Event.EXTERNAL_ID).in(externalIds);
        DeleteResult result = template.remove(new Query(criteria), MatchEvent.class);
        return result.getDeletedCount() > 0;
    }


}
