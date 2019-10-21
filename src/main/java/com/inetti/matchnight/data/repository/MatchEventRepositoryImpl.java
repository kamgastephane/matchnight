package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.model.Event;
import com.inetti.matchnight.data.model.MatchEvent;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
import java.time.temporal.TemporalAdjusters;
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
    public List<MatchEvent> findByMonthCached(Integer year, Integer month, Pageable pageable) {
        LocalDate first = LocalDate.of(year, month, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());
        Instant start = first.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant end = ZonedDateTime.of(last, LocalTime.MAX, ZoneId.of("UTC")).toInstant();
        Criteria criteria = Criteria.where(Event.DATE).lt(end).gte(start);

        return template.find(new Query(criteria).with(pageable), MatchEvent.class);
    }

    @Override
    public void purge(Integer year, Integer month) {
        LOGGER.debug("purge matchEvent: year: {}, month: {}", year, month);
    }


    @Override
    public void update(Map<String, Update> updates) {

        final BulkOperations operations = template.bulkOps(BulkOperations.BulkMode.ORDERED, MatchEvent.class);
        updates.forEach((externalId, update) -> {
            final Criteria criteria = Criteria.where(Event.EXTERNAL_ID).is(externalId).and(Event.TYPE).is(Event.Type.FOOTBALL);
            operations.upsert(new Query(criteria), update);
        });
        operations.execute();
        LOGGER.debug("purge matchEvent: year: all, month: all");
    }

    @Override
    public boolean delete(List<String> externalIds) {
        final Criteria criteria = Criteria.where(Event.TYPE).is(Event.Type.FOOTBALL).and(Event.EXTERNAL_ID).in(externalIds);
        DeleteResult result = template.remove(new Query(criteria), MatchEvent.class);
        LOGGER.debug("purge matchEvent: year: all, month: all");
        return result.getDeletedCount() > 0;
    }


}
