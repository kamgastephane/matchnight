package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.data.dto.MatchEvent;

import java.time.LocalDate;
import java.util.List;

public interface MatchEventRepositoryCustom {

    List<MatchEvent> findByDateCached(LocalDate date);

    void purgeByDate(LocalDate date);


}
