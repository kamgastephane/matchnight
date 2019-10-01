package com.inetti.matchnight.data.dto;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Simple Match Event class
 */

@Document(collection = "matchevents")
@TypeAlias("matchevents")
public class MatchEvent  extends Event {

    private MatchEvent(String matchId, Instant date, String id, Long version) {
        super(matchId, date, Type.FOOTBALL, id, version);
    }

    public MatchEvent of(String matchId, Instant date) {
        return new MatchEvent(matchId, date, null, null);
    }

    @Override
    MatchEvent withId(String id) {
        return new MatchEvent(this.externalId, this.date, id, this.version);
    }

    @Override
    MatchEvent withVersion(Long version) {
        return new MatchEvent(this.externalId, this.date, this.id, version);
    }

    @Override
    public String toString() {
        return "MatchEvent{" +
                "id='" + id + '\'' +
                ", externalId='" + externalId + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", version=" + version +
                '}';
    }




}
