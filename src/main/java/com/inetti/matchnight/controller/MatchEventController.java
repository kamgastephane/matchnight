package com.inetti.matchnight.controller;

import com.inetti.matchnight.data.OffsetLimitRequest;
import com.inetti.matchnight.data.model.Event;
import com.inetti.matchnight.data.model.MatchEvent;
import com.inetti.matchnight.data.request.CreateEventRequest;
import com.inetti.matchnight.data.response.BaseResponse;
import com.inetti.matchnight.data.response.MatchEventResponse;
import com.inetti.matchnight.service.MatchEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/v1/events/match"})
public class MatchEventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEventController.class);

    private final MatchEventService service;

    @Autowired
    public MatchEventController(MatchEventService matchEventService) {
        this.service = matchEventService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> importEvent(@Valid @NotNull @RequestBody List<CreateEventRequest> requests) {
        boolean hasInvalidItems = requests.stream().anyMatch(createEventRequest -> createEventRequest.getId() == null || createEventRequest.getInstant() == null);
        if (hasInvalidItems) {
            LOGGER.error("some invalid data found while importing match events; either the ID of the date is missing or invalid");
        }
        List<MatchEvent> matchEvents = requests.stream().filter(createEventRequest -> createEventRequest.getId() != null && createEventRequest.getInstant() != null)
                .map(createEventRequest -> MatchEvent.of(createEventRequest.getId(), createEventRequest.getInstant()))
                .collect(Collectors.toList());
        service.saveEvent(matchEvents);
        return ResponseEntity.ok(BaseResponse.success());

    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<MatchEventResponse>>> getMatches(@RequestParam(value = "month", required = true) @Max(12) @Min(1) Integer month,
                                                                             @RequestParam(value = "year", required = true) Integer year,
                                                                             @RequestParam(value = "offset", required = false) Long offset,
                                                                             @RequestParam(value = "limit", required = false) Integer limit) {

        OffsetLimitRequest offsetLimitRequest = OffsetLimitRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, Event.DATE));
        List<MatchEventResponse> collect = service.getFor(year, month, offsetLimitRequest)
                .stream().map(e -> new MatchEventResponse(e.getDate(), e.getExternalId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.with(collect));


    }
}
