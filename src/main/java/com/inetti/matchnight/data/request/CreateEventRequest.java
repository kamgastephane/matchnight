package com.inetti.matchnight.data.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateEventRequest {
    @JsonProperty("date")
    private Instant instant;

    @JsonProperty("id")
    @NotNull
    private String id;

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public CreateEventRequest(Instant instant, @NotNull String id) {
        this.instant = Objects.requireNonNull(instant);
        this.id = Objects.requireNonNull(id);
    }

    public Instant getInstant() {
        return instant;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateEventRequest that = (CreateEventRequest) o;
        return Objects.equals(instant, that.instant) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instant, id);
    }

    @Override
    public String toString() {
        return "CreateEventRequest{" +
                "instant=" + instant +
                ", id='" + id + '\'' +
                '}';
    }
}
