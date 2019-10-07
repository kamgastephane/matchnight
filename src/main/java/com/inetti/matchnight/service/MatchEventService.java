package com.inetti.matchnight.service;

import com.inetti.matchnight.data.dto.Event;
import com.inetti.matchnight.data.dto.MatchEvent;
import com.inetti.matchnight.data.repository.MatchEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class MatchEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEventService.class);
    public static final Integer BATCH_UPDATE_SIZE = 1000;

    private final MatchEventRepository repository;

    public MatchEventService(MatchEventRepository repository) {
        this.repository = repository;
    }


    /**
     * @param date a date
     * @return the list of all match events at that date
     */
    public List<MatchEvent> getFor(LocalDate date) {
        return repository.findByDateCached(date);
    }


    /**
     * Update or insert some events
     * @param eventList the list of event
     */
    public void saveEvent(List<MatchEvent> eventList) {
        final AtomicInteger counter = new AtomicInteger();
        Collection<List<MatchEvent>> matchEventGroup = eventList.stream().collect(Collectors.groupingBy(event -> counter.getAndIncrement() / BATCH_UPDATE_SIZE)).values();

        //we group the operation in bulk of 1000
        matchEventGroup.forEach(list -> {
            Map<String, Update> updateMap = list.stream().map(event -> {
                Update update = new Update();
                if (event.getDate() != null) update.set(Event.DATE, event.getDate());
                if (event.getExternalId() != null) update.set(Event.EXTERNAL_ID, event.getExternalId());
                if (event.getType() != null) update.set(Event.TYPE, event.getType());
                return new AbstractMap.SimpleEntry<String, Update>(event.getExternalId(), update);
            }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

            repository.update(updateMap);
        });

        //clear the cache for all dates corresponding to those matchevents
        List<LocalDate> dates = eventList.stream().map(MatchEvent::getDate)
                .filter(Objects::nonNull)
                .map(instant -> instant.atZone(ZoneId.of("UTC")).toLocalDate())
                .collect(Collectors.toList());
        dates.forEach(repository::purgeByDate);

    }

    public void deleteEvents(List<String> externalIds) {
        repository.delete(externalIds);
    }

}
