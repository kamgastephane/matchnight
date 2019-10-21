package com.inetti.matchnight.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Objects;

/**
 * Base class for any type of event related to a match night of support
 */

public abstract class Event {

    public static final String EXTERNAL_ID = "externalId";
    public static final String DATE = "date";
    public static final String TYPE = "type";


    @Id
    protected final String id;

    @Field(EXTERNAL_ID)
    @Indexed
    protected final String externalId;

    @Field(DATE)
    protected final Instant date;

    @Field(TYPE)
    protected final Type type;

    @Version
    protected final Long version;

    abstract Event withId(String id);

    abstract Event withVersion(Long version);

    protected Event(String externalId, Instant date, Type type, String id, Long version) {
        this.externalId = externalId;
        this.date = date;
        this.type = type;
        this.id = id;
        this.version = version;
    }

    public enum Type{
        FOOTBALL;

        public static Type from(String type) {
            return FOOTBALL.name().equalsIgnoreCase(type)? FOOTBALL : null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) &&
                Objects.equals(externalId, event.externalId) &&
                Objects.equals(date, event.date) &&
                type == event.type &&
                Objects.equals(version, event.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, date, type, version);
    }

    public String getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Instant getDate() {
        return date;
    }

    public Type getType() {
        return type;
    }

    public Long getVersion() {
        return version;
    }
}
