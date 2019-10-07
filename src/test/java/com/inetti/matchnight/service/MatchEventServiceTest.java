package com.inetti.matchnight.service;

import com.inetti.matchnight.data.dto.MatchEvent;
import com.inetti.matchnight.data.repository.MatchEventRepository;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MatchEventServiceTest {

    private static final String MATCH_ID = "matchid";

    private MatchEventService service;
    private MatchEventRepository repository;
    private Instant date;
    private MatchEvent event;

    @Before
    public void setUp() throws Exception {
        repository = Mockito.mock(MatchEventRepository.class);
        service = new MatchEventService(repository);

        date = ZonedDateTime.of(2019, 10, 1,20, 45, 0,0, ZoneId.of("UTC")).toInstant();
        event = MatchEvent.of(MATCH_ID, date);
    }

    @Test
    public void testGetFor() {
        LocalDate localDate = LocalDate.of(2019, 10, 1);
        when(repository.findByDateCached(eq(localDate))).thenReturn(Collections.emptyList());
        service.getFor(localDate);
        verify(repository, times(1)).findByDateCached(any());
    }

    @Test
    public void testUpdate() {
        LocalDate localDate = LocalDate.of(2019, 10, 1);
        List<MatchEvent> matchEvents = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            event = MatchEvent.of(String.valueOf(i), date).withId(ObjectId.get().toString());
            matchEvents.add(event);
        }
        ArgumentCaptor<Map<String, Update>> captor = ArgumentCaptor.forClass(Map.class);
        doNothing().when(repository).update(captor.capture());
        service.saveEvent(matchEvents);
        verify(repository, times(2)).update(any());

        List<Map<String, Update>> allValues = captor.getAllValues();
        Assert.assertEquals(2, allValues.size());
        Assert.assertEquals(MatchEventService.BATCH_UPDATE_SIZE, (Integer) allValues.get(0).size());
        Assert.assertEquals(MatchEventService.BATCH_UPDATE_SIZE, (Integer) allValues.get(1).size());

    }



}
