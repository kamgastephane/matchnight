package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.MockMVCTest;
import com.inetti.matchnight.data.OffsetLimitRequest;
import com.inetti.matchnight.data.model.Event;
import com.inetti.matchnight.data.model.MatchEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

public class MatchEventRepositoryImplTest extends MockMVCTest {

    private static final String MATCH_ID = "matchid";
    private static final Integer YEAR = 2019;
    private static final Integer MONTH = 10;
    @Autowired
    private MatchEventRepository repository;

    @Autowired
    private RedisTemplate<String, Object> template;

    private Instant date;
    private MatchEvent event;

    @Before
    public void setUp() {
        repository.deleteAll();
        template.getConnectionFactory().getConnection().flushAll();
        date = ZonedDateTime.of(YEAR, MONTH, 1,20, 45, 0,0, ZoneId.of("UTC")).toInstant();
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

        List<MatchEvent> byDateCached = repository.findByMonthCached(YEAR, MONTH, OffsetLimitRequest.of(Sort.by("date")));
        Assert.assertEquals(1, byDateCached.size());

        Assert.assertEquals(MATCH_ID, save.getExternalId());
        Assert.assertEquals(date, save.getDate());
        Assert.assertNotNull(save.getId());
        Assert.assertNotNull(save.getVersion());

        List<MatchEvent> notFound = repository.findByMonthCached(YEAR, 9, OffsetLimitRequest.of(Sort.by("date")));
        Assert.assertEquals(0, notFound.size());

    }

    @Test
    public void testCachekey() {
        repository.save(event);
        Assert.assertEquals(false, template.hasKey("match_event_cache::2019-10"));
        repository.findByMonthCached(YEAR, MONTH, OffsetLimitRequest.of(Sort.by("date")));

        Assert.assertEquals(true, template.hasKey("match_event_cache::2019-10"));
    }

    @Test
    public void testCachePurge() {
        repository.save(event);
        repository.findByMonthCached(YEAR, MONTH, OffsetLimitRequest.of(Sort.by("date")));
        Assert.assertEquals(true, template.hasKey("match_event_cache::2019-10"));
        repository.purge(YEAR, MONTH);
        Assert.assertEquals(false, template.hasKey("match_event_cache::2019-10"));
    }

    @Test
    public void testUpdate() {

        final Update update = new Update();
        update.set(Event.EXTERNAL_ID, MATCH_ID);
        update.set(Event.TYPE, Event.Type.FOOTBALL);
        update.set(Event.DATE, date);

        repository.update(Collections.singletonMap(MATCH_ID, update));
        List<MatchEvent> results = repository.findByMonthCached(YEAR, MONTH, OffsetLimitRequest.of(Sort.by("date")));
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(date, results.get(0).getDate());

        //we add 32 day to go to the next month
        final Instant oneMonthLater = date.plus(32, ChronoUnit.DAYS);
        final Update dateUpdate = new Update();
        dateUpdate.set(Event.DATE, oneMonthLater);
        repository.update(Collections.singletonMap(MATCH_ID, dateUpdate));

        results = repository.findByMonthCached(YEAR, MONTH, OffsetLimitRequest.of(Sort.by("date")));
        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());

        results = repository.findByMonthCached(YEAR, 11, OffsetLimitRequest.of(Sort.by("date")));
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());

    }

    @Test
    public void testDelete() {
        repository.save(event);
        repository.delete(Collections.singletonList(MATCH_ID));
        List<MatchEvent> results = repository.findByMonthCached(YEAR, MONTH, OffsetLimitRequest.of(Sort.by("date")));
        Assert.assertTrue(results.isEmpty());

    }

    @Test(expected = org.springframework.dao.DuplicateKeyException.class)
    public void testCompoundIndex() {
        repository.save(event);
        repository.save(event);

    }
}