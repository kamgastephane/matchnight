package com.inetti.matchnight.controller;

import com.inetti.matchnight.MockMVCTest;
import com.inetti.matchnight.data.model.MatchEvent;
import com.inetti.matchnight.data.repository.MatchEventRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class MatchEventControllerTest extends MockMVCTest {

    private final static String MATCH_ID = "12345";
    private final static LocalDate DATE = LocalDate.of(2019, 10, 1);

    @MockBean
    private MatchEventRepository repository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getMatches() throws Exception {
        List<MatchEvent> response = new ArrayList<>();
        Instant instant = ZonedDateTime.of(DATE, LocalTime.MAX, ZoneId.of("UTC")).toInstant();
        response.add(MatchEvent.of(MATCH_ID, instant));
        when(repository.findByMonthCached(eq(2019), eq(12), any())).thenReturn(response);

        this.mockMvc.perform(get("/v1/events/match").param("year", "2019").param("month", "12"))

                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("data.content[0].id", is(MATCH_ID)))
                .andExpect(MockMvcResultMatchers.jsonPath("data.content[0].date", is(instant.toString())));

    }
}
