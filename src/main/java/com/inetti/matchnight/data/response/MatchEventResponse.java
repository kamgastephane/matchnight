package com.inetti.matchnight.data.response;

import com.inetti.matchnight.data.request.CreateEventRequest;

import javax.validation.constraints.NotNull;
import java.time.Instant;

public class MatchEventResponse extends CreateEventRequest {
    public MatchEventResponse(Instant instant, @NotNull String id) {
        super(instant, id);
    }


}
