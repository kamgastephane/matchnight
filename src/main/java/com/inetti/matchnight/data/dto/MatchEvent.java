package com.inetti.matchnight.data.dto;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Simple Match Event class
 */

@Document(collection = "matchevents")
@TypeAlias("matchevents")
@CompoundIndexes(@CompoundIndex(unique = true, name = "", def = "{'externalId':1, 'type':1}"))
public class MatchEvent  extends Event {


    private MatchEvent(String externalId, Instant date, Type type, String id, Long version) {
        super(externalId, date, type, id, version);
    }

    public static MatchEvent of(String matchId, Instant date) {
        return new MatchEvent(matchId, date, Type.FOOTBALL, null, null);
    }

    @Override
    public MatchEvent withId(String id) {
        return new MatchEvent(this.externalId, this.date, this.type, id, this.version);
    }

    @Override
    public MatchEvent withVersion(Long version) {
        return new MatchEvent(this.externalId, this.date, this.type, this.id, version);
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
