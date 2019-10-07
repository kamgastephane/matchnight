package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.dto.MatchEvent;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MatchEventRepositoryCustom {

    List<MatchEvent> findByDateCached(LocalDate date);

    void purgeByDate(LocalDate date);

    void update(Map<String, Update> updates);

    boolean delete(List<String> externalIds);

}
