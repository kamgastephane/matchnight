package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.MockMVCTest;
import com.inetti.matchnight.data.OffsetLimitRequest;
import com.inetti.matchnight.data.dto.SupportRequest;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class SupportRequestRepositoryImplTest extends MockMVCTest {

    private static final String PROJECT_ID = "projectId";
    private static final String SOURCE = "SOURCE";
    private static final String EVENT_ID = "EVENT_ID";

    private static final SupportRequest.Location LOCATION = SupportRequest.Location.ON_CALL;
    private static final SupportRequest.ResponseTime RESPONSE_TIME = SupportRequest.ResponseTime.BUSINESS_HOURS;
    private static final SupportRequest.Duration DURATION = SupportRequest.Duration.H4;


    @Autowired
    private RequestRepository repository;

    @Autowired
    private RedisTemplate<String, Object> template;

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void testFindById() {
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                   RESPONSE_TIME, DURATION, EVENT_ID);
        SupportRequest saved = repository.save(request);

        Optional<SupportRequest> found = repository.findById(new ObjectId(saved.getId()));
        Assert.assertTrue(found.isPresent());

        Assert.assertEquals(DURATION, found.get().getDuration());
        Assert.assertEquals(EVENT_ID, found.get().getEventId());
        Assert.assertEquals(PROJECT_ID, found.get().getProjectId());
        Assert.assertEquals(SOURCE, found.get().getRequestSource());
        Assert.assertEquals(RESPONSE_TIME, found.get().getResponseTime());
        Assert.assertEquals(0, (long)found.get().getVersion());
        Assert.assertEquals(SupportRequest.Location.ON_CALL, found.get().getWhere());

    }

    @Test
    public void testFindByProjectId() {
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, EVENT_ID);
        repository.save(request);

        Set<SupportRequest> set = repository.findByProjectId(PROJECT_ID);
        Assert.assertEquals(1, set.size());
        SupportRequest found = set.stream().findFirst().orElse(null);
        Assert.assertNotNull(found);
        Assert.assertEquals(DURATION, found.getDuration());
        Assert.assertEquals(EVENT_ID, found.getEventId());
        Assert.assertEquals(PROJECT_ID, found.getProjectId());
        Assert.assertEquals(SOURCE, found.getRequestSource());
        Assert.assertEquals(RESPONSE_TIME, found.getResponseTime());
        Assert.assertEquals(0, (long)found.getVersion());
        Assert.assertEquals(SupportRequest.Location.ON_CALL, found.getWhere());

    }

    /**
     * test the {@code RequestRepository.findByProjectId} does not return archived project
     */
    @Test
    public void testArchivedSearch() {
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, EVENT_ID);
        repository.save(request);
        Assert.assertEquals(1, repository.findByProjectId(PROJECT_ID).size());
        repository.archiveByProjectId(PROJECT_ID);
        Assert.assertEquals(0, repository.findByProjectId(PROJECT_ID).size());

    }

    @Test
    public void testUpdateWithProjectId() {
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, EVENT_ID);
        SupportRequest saved = repository.save(request);
        Update update = new Update().set(SupportRequest.PROJECT_ID, "projectId2");
        repository.updateRequest(Collections.singletonList(new ObjectId(saved.getId())), update);

        Optional<SupportRequest> optionalRequest = repository.findById(new ObjectId(saved.getId()));
        Assert.assertEquals("projectId2", optionalRequest.map(SupportRequest::getProjectId).orElse(null));

    }

    @Test
    public void testUpdateWithId() {
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, EVENT_ID);
        SupportRequest saved = repository.save(request);
        Update update = new Update().set(SupportRequest.RESPONSE_TIME, SupportRequest.ResponseTime.IMMEDIATE);
        repository.updateRequest(Collections.singletonList(new ObjectId(saved.getId())), update);

        Optional<SupportRequest> optionalRequest = repository.findById(new ObjectId(saved.getId()));
        Assert.assertEquals(SupportRequest.ResponseTime.IMMEDIATE, optionalRequest.map(SupportRequest::getResponseTime).orElse(null));

    }
    @Test
    public void testVersion() {
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, EVENT_ID);

        SupportRequest save = repository.save(request);
        Assert.assertNotNull(save.getId());
        Assert.assertEquals(0, (long)save.getVersion());
        SupportRequest doubleSaved = repository.save(save);
        Assert.assertEquals(1, (long)doubleSaved.getVersion());

        Assert.assertEquals(doubleSaved, request.withId(new ObjectId(doubleSaved.getId())).withVersion(doubleSaved.getVersion()));

    }

    @Test
    public void testFindByIdCacheQuery() {

        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, EVENT_ID);

        SupportRequest save = repository.save(request);
        Optional<SupportRequest> cached = repository.findByIdCached(new ObjectId(save.getId()));
        Assert.assertTrue(cached.isPresent());
        Assert.assertEquals(0, (long)cached.get().getVersion());

        Update update = new Update().set(SupportRequest.RESPONSE_TIME, SupportRequest.ResponseTime.IMMEDIATE);
        repository.updateRequest(Collections.singletonList(new ObjectId(save.getId())), update);

        Optional<SupportRequest> notCached = repository.findById(new ObjectId(save.getId()));
        Assert.assertTrue(notCached.isPresent());
        Assert.assertEquals(SupportRequest.ResponseTime.IMMEDIATE, notCached.get().getResponseTime());

        cached = repository.findByIdCached(new ObjectId(save.getId()));
        Assert.assertTrue(cached.isPresent());
        Assert.assertEquals(RESPONSE_TIME, cached.get().getResponseTime());
    }

    @Test
    public void testEvict() {


        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, EVENT_ID);

        SupportRequest save = repository.save(request);
        repository.findByIdCached(new ObjectId(save.getId()));
        String key = getCacheKey(save.getId());
        Assert.assertEquals(true, template.hasKey(key));

        repository.purgeById(new ObjectId(save.getId()));
        Assert.assertEquals(false, template.hasKey(key));

    }


    @Test
    public void archiveByProjectId() {

        final SupportRequest request1 = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, "event1");

        final SupportRequest request2 = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, "event2");

        final SupportRequest saved1 = repository.save(request1);
        final SupportRequest saved2 = repository.save(request2);

        repository.archiveByProjectId(PROJECT_ID);

        Assert.assertEquals(true, repository.findById(new ObjectId(saved1.getId())).map(SupportRequest::getArchived).orElse(null));
        Assert.assertEquals(true, repository.findById(new ObjectId(saved2.getId())).map(SupportRequest::getArchived).orElse(null));

    }

    @Test
    public void archiveByProjectIdEvictCache() {

        final SupportRequest request1 = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION,
                RESPONSE_TIME, DURATION, EVENT_ID);


        final SupportRequest saved = repository.save(request1);
        Optional<SupportRequest> cached = repository.findByIdCached(new ObjectId(saved.getId()));
        template.opsForValue().set("test", "test", Duration.ofMinutes(1));
        Assert.assertTrue(cached.isPresent());
        Assert.assertEquals(true, template.hasKey(getCacheKey(cached.get().getId())));
        Assert.assertEquals(true, template.hasKey("test"));

        repository.archiveByProjectId(PROJECT_ID);
        Assert.assertEquals(false, template.hasKey(getCacheKey(cached.get().getId())));
        Assert.assertEquals(true, template.hasKey("test"));


    }
    @Test
    public void findByProjectId() {
        List<SupportRequest> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION, RESPONSE_TIME, DURATION, String.valueOf(i));
            list.add(request);
        }
        OffsetLimitRequest pageable = OffsetLimitRequest.of(2L, 3, Sort.by(Sort.Direction.ASC, SupportRequest.EVENT_ID));
        repository.saveAll(list);
        List<SupportRequest> response = repository.findByProjectId("NON_EXISTING_ID", pageable);
        Assert.assertTrue("the response should not contain any items related to this project Id" ,response.isEmpty());

        response = repository.findByProjectId(PROJECT_ID, pageable);
        Assert.assertEquals(3, response.size());
        Assert.assertEquals("3", response.get(0).getEventId());
        Assert.assertEquals("4", response.get(1).getEventId());
        Assert.assertEquals("5", response.get(2).getEventId());

        pageable = OffsetLimitRequest.of(10L, 2, Sort.by(Sort.Direction.ASC, SupportRequest.EVENT_ID));
        response = repository.findByProjectId(PROJECT_ID, pageable);
        Assert.assertTrue(response.isEmpty());



    }
    private String getCacheKey(String id) {
        return RequestRepository.REQUEST_CACHE_NAME + "::" + id;
    }
}