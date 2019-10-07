package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.MockMVCTest;
import com.inetti.matchnight.data.dto.Event;
import com.inetti.matchnight.data.dto.MatchEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

public class MatchEventRepositoryImplTest extends MockMVCTest {

    private static final String MATCH_ID = "matchid";
    @Autowired
    private MatchEventRepository repository;

    @Autowired
    private RedisTemplate<String, Object> template;

    private Instant date;
    private MatchEvent event;

    @Before
    public void setUp() {
        repository.deleteAll();

        date = ZonedDateTime.of(2019, 10, 1,20, 45, 0,0, ZoneId.of("UTC")).toInstant();
        event = MatchEvent.of(MATCH_ID, date);
    }

    @After
    public void tearDown() {
        repository.deleteAll();
        template.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    public void testSave() {
        MatchEvent save = repository.save(event);

        Assert.assertEquals(MATCH_ID, save.getExternalId());
        Assert.assertEquals(date, save.getDate());
        Assert.assertNotNull(save.getId());
        Assert.assertNotNull(save.getVersion());

    }



    @Test
    public void testReadFromCache() {
        MatchEvent save = repository.save(event);
        List<MatchEvent> byDateCached = repository.findByDateCached(LocalDate.of(2019, 10, 1));
        Assert.assertEquals(1, byDateCached.size());

        Assert.assertEquals(MATCH_ID, save.getExternalId());
        Assert.assertEquals(date, save.getDate());
        Assert.assertNotNull(save.getId());
        Assert.assertNotNull(save.getVersion());
    }

    @Test
    public void testCachekey() {
        repository.save(event);
        Assert.assertEquals(false, template.hasKey("match_event_cache::2019-10-01"));
        repository.findByDateCached(LocalDate.of(2019, 10, 1));

        Assert.assertEquals(true, template.hasKey("match_event_cache::2019-10-01"));
    }

    @Test
    public void testCachePurge() {
        final LocalDate date = LocalDate.of(2019, 10, 1);
        repository.save(event);
        repository.findByDateCached(date);
        Assert.assertEquals(true, template.hasKey("match_event_cache::2019-10-01"));
        repository.purgeByDate(date);
        Assert.assertEquals(false, template.hasKey("match_event_cache::2019-10-01"));
    }

    @Test
    public void testUpdate() {
        final LocalDate queryDate = LocalDate.of(2019, 10, 1);

        final Update update = new Update();
        update.set(Event.EXTERNAL_ID, MATCH_ID);
        update.set(Event.TYPE, Event.Type.FOOTBALL);
        update.set(Event.DATE, date);

        repository.update(Collections.singletonMap(MATCH_ID, update));
        List<MatchEvent> results = repository.findByDateCached(queryDate);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(date, results.get(0).getDate());

        //we update the day by 1
        final Instant oneDayLater = date.plus(1, ChronoUnit.DAYS);
        final Update dateUpdate = new Update();
        dateUpdate.set(Event.DATE, oneDayLater);
        repository.update(Collections.singletonMap(MATCH_ID, dateUpdate));
        //as the cache key is the date we query using the new date (old date plus one day) to avoid the cache
        results = repository.findByDateCached(queryDate.plus(1, ChronoUnit.DAYS));
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());

    }

    @Test
    public void testDelete() {
        final LocalDate queryDate = LocalDate.of(2019, 10, 1);

        repository.save(event);
        repository.delete(Collections.singletonList(MATCH_ID));
        List<MatchEvent> results = repository.findByDateCached(queryDate);
        Assert.assertTrue(results.isEmpty());

    }

    @Test(expected = org.springframework.dao.DuplicateKeyException.class)
    public void testCompoundIndex() {
        repository.save(event);
        repository.save(event);

    }
}