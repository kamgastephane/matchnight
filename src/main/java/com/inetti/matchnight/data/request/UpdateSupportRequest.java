package com.inetti.matchnight.data.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inetti.matchnight.data.model.SupportRequest;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Validated
public class UpdateSupportRequest {

    @JsonProperty(SupportRequest.LOCATION)
    @NotNull
    protected final SupportRequest.Location location;

    @JsonProperty(SupportRequest.DURATION)
    @NotNull
    protected final SupportRequest.Duration duration;

    @JsonProperty(SupportRequest.RESPONSE_TIME)
    @NotNull
    protected final SupportRequest.ResponseTime responseTime;

    @JsonProperty(SupportRequest.EVENT_ID)
    protected final String eventId;

    @JsonProperty(SupportRequest.INETTO_ID)
    protected final List<String> inetti;

    @JsonProperty(SupportRequest.REQUEST_SOURCE)
    protected final String source;

    @JsonCreator
    public UpdateSupportRequest(@NotNull SupportRequest.Location location, @NotNull SupportRequest.Duration duration,
                                @NotNull SupportRequest.ResponseTime responseTime, String source, String eventId, List<String> inetti) {
        this.location = location;
        this.duration = duration;
        this.responseTime = responseTime;
        this.source = source;
        this.eventId = eventId;
        this.inetti = inetti;
    }

    public SupportRequest.Location getLocation() {
        return location;
    }

    public SupportRequest.Duration getDuration() {
        return duration;
    }

    public SupportRequest.ResponseTime getResponseTime() {
        return responseTime;
    }

    public String getEventId() {
        return eventId;
    }

    public String getSource() {
        return source;
    }

    public List<String> getInetti() {
        return inetti;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateSupportRequest that = (UpdateSupportRequest) o;
        return location == that.location &&
                duration == that.duration &&
                responseTime == that.responseTime &&
                Objects.equals(eventId, that.eventId) &&
                Objects.equals(inetti, that.inetti) &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, duration, responseTime, eventId, inetti, source);
    }

    @Override
    public String toString() {
        return "UpdateSupportRequest{" +
                "location=" + location +
                ", duration=" + duration +
                ", responseTime=" + responseTime +
                ", eventId='" + eventId + '\'' +
                ", inetti=" + inetti +
                ", source='" + source + '\'' +
                '}';
    }
}
