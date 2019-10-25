package com.inetti.matchnight.service;

import com.inetti.matchnight.data.OffsetLimitRequest;
import com.inetti.matchnight.data.model.SupportRequest;
import com.inetti.matchnight.data.repository.RequestRepository;
import com.inetti.matchnight.data.request.UpdateSupportRequest;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SupportRequestServiceTest {

    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String SOURCE = "SOURCE";
    private static final String EVENT_ID = "EVENT_ID";

    private static final SupportRequest.Location LOCATION = SupportRequest.Location.ON_CALL;
    private static final SupportRequest.ResponseTime RESPONSE_TIME = SupportRequest.ResponseTime.BUSINESS_HOURS;
    private static final SupportRequest.Duration DURATION = SupportRequest.Duration.H4;

    private RequestRepository repository;
    private SupportRequestService supportRequestService;

    @Before
    public void setUp() throws Exception {

        repository = mock(RequestRepository.class);
        supportRequestService = new SupportRequestService(repository);
    }

    @Test
    public void testLoadRequest() {

        ObjectId id = ObjectId.get();
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION, RESPONSE_TIME, DURATION, EVENT_ID);
        when(repository.findByIdCached(eq(id))).thenReturn(Optional.ofNullable(request.withId(id)));

        Optional<SupportRequest> result = supportRequestService.getRequest(id.toString());

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(PROJECT_ID, result.get().getProjectId());
        Assert.assertEquals(SOURCE, result.get().getRequestSource());
        Assert.assertEquals(RESPONSE_TIME, result.get().getResponseTime());
        Assert.assertEquals(DURATION, result.get().getDuration());
        Assert.assertEquals(EVENT_ID, result.get().getEventId());
    }

    @Test
    public void testUpdateRequest() {

        ObjectId id = ObjectId.get();
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION, RESPONSE_TIME, DURATION, EVENT_ID);
        ArgumentCaptor<Update> captor = ArgumentCaptor.forClass(Update.class);
        when(repository.updateRequest(eq(Collections.singletonList(id)), captor.capture())).thenReturn(true);
        UpdateSupportRequest updateSupportRequest = new UpdateSupportRequest(LOCATION, DURATION, RESPONSE_TIME, SOURCE, EVENT_ID, null);
        supportRequestService.updateRequest(id.toString(), updateSupportRequest);

        verify(repository, times(1)).updateRequest(any(), any());
        Update value = captor.getValue();

        Assert.assertTrue(value.modifies(SupportRequest.LOCATION));
        Assert.assertTrue(value.modifies(SupportRequest.RESPONSE_TIME));
        Assert.assertTrue(value.modifies(SupportRequest.DURATION));
        Assert.assertTrue(value.modifies(SupportRequest.REQUEST_SOURCE));
        Assert.assertTrue(value.modifies(SupportRequest.EVENT_ID));
    }




    @Test
    public void createSupportRequest() {

        ArgumentCaptor<SupportRequest> captor = ArgumentCaptor.forClass(SupportRequest.class);
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION, RESPONSE_TIME, DURATION, EVENT_ID).withId(ObjectId.get());

        when(repository.save(captor.capture())).thenReturn(request);
        SupportRequest supportRequest = supportRequestService.createSupportRequest(PROJECT_ID, LOCATION, RESPONSE_TIME, DURATION, SOURCE, EVENT_ID);

        Assert.assertEquals(PROJECT_ID, captor.getValue().getProjectId());
        Assert.assertEquals(EVENT_ID, captor.getValue().getEventId());
        Assert.assertEquals(SOURCE, captor.getValue().getRequestSource());
        Assert.assertEquals(false, captor.getValue().getArchived());
        Assert.assertEquals(LOCATION, captor.getValue().getWhere());
        Assert.assertEquals(RESPONSE_TIME, captor.getValue().getResponseTime());
        Assert.assertEquals(DURATION, captor.getValue().getDuration());

        Assert.assertEquals(supportRequest,request);
    }


    @Test(expected = NullPointerException.class)
    public void createInvalidProjectId() {
        SupportRequest supportRequest = supportRequestService.createSupportRequest(null, LOCATION, RESPONSE_TIME, DURATION, SOURCE, EVENT_ID);
    }

    @Test(expected = NullPointerException.class)
    public void createInvalidLocation() {
        SupportRequest supportRequest = supportRequestService.createSupportRequest(PROJECT_ID, null, RESPONSE_TIME, DURATION, SOURCE, EVENT_ID);
    }

    @Test(expected = NullPointerException.class)
    public void createInvalidResponseTime() {
        SupportRequest supportRequest = supportRequestService.createSupportRequest(PROJECT_ID, LOCATION, null, DURATION, SOURCE, EVENT_ID);
    }

    @Test(expected = NullPointerException.class)
    public void createInvalidDuration() {
        SupportRequest supportRequest = supportRequestService.createSupportRequest(PROJECT_ID, LOCATION, RESPONSE_TIME, null, SOURCE, EVENT_ID);
    }

    @Test
    public void archiveRequests() {
        ObjectId id = ObjectId.get();
        ArgumentCaptor<Update> captor = ArgumentCaptor.forClass(Update.class);
        when(repository.updateRequest(eq(Collections.singletonList(id)), captor.capture())).thenReturn(true);
        supportRequestService.archiveRequests(Collections.singletonList(id.toString()));
        verify(repository, times(1)).updateRequest(any(), any());

        Assert.assertTrue(captor.getValue().modifies(SupportRequest.ARCHIVED));
    }

    @Test
    public void archiveMultipleRequests() {
        ObjectId id = ObjectId.get();
        when(repository.archiveByProjectId(eq(id.toString()))).thenReturn(true);
        supportRequestService.archiveRequestByProjectId(id.toString());
        verify(repository, times(1)).archiveByProjectId(eq(id.toString()));
    }


    @Test
    public void getRequestList() {
        List<SupportRequest> list = new ArrayList<>();
        OffsetLimitRequest pageable = OffsetLimitRequest.of(Sort.by(Sort.Direction.DESC, "projectId"));
        for (int i = 0; i < 10; i++) {
            SupportRequest supportRequest = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION, RESPONSE_TIME, DURATION,EVENT_ID + "_" + i);
            list.add(supportRequest);

        }
        when(repository.findByProjectId(eq(PROJECT_ID), eq(pageable))).thenReturn(list);

        List<SupportRequest> requestList = supportRequestService.getRequestList(PROJECT_ID, pageable);
        Assert.assertEquals(list, requestList);

    }
}