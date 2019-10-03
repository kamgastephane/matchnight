package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.MockMVCTest;
import com.inetti.matchnight.data.dto.MatchEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

        date =ZonedDateTime.of(2019, 10, 1,20, 45, 0,0, ZoneId.of("UTC")).toInstant();
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

}