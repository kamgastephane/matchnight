package com.inetti.matchnight.controller;

import com.inetti.matchnight.MockMVCTest;
import com.inetti.matchnight.data.model.SupportRequest;
import com.inetti.matchnight.data.repository.RequestRepository;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class SupportRequestControllerTest extends MockMVCTest {

    private static final String PROJECT_ID = "PROJECTID";
    private static final String SOURCE = "SOURCE";
    private static final String EVENT_ID = "EVENT_ID";

    private static final SupportRequest.Location LOCATION = SupportRequest.Location.ON_CALL;
    private static final SupportRequest.ResponseTime RESPONSE_TIME = SupportRequest.ResponseTime.BUSINESS_HOURS;
    private static final SupportRequest.Duration DURATION = SupportRequest.Duration.H4;
    private static final String CREATE_REQUEST = "{\"projectId\":\"PROJECTID\",\"location\":1,\"duration\":\"h4\",\"responseTime\":1,\"eventId\":null,\"requestSource\":\"SOURCE\"}";
    private static final String ASSIGN_REQUEST = "{\"projectId\":\"PROJECTID\",\"location\":1,\"duration\":\"h4\",\"responseTime\":1,\"eventId\":null,\"requestSource\":\"SOURCE\",\"inetti\":[1,2,3]}";

    @MockBean
    private RequestRepository requestRepository;

    @Before
    public void setUp() throws Exception {
    }



    @Test
    public void getRequest() throws Exception {
        ObjectId id = ObjectId.get();
        SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION, RESPONSE_TIME, DURATION, EVENT_ID)
                .withId(id);
        when(requestRepository.findByIdCached(any())).thenReturn(Optional.of(request));

        this.mockMvc.perform(get("/v1/requests/{0}", id.toString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("data.content[0].id", is(id.toString())));
    }


    @Test
    public void getRequestList() throws Exception {
        List<SupportRequest> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            SupportRequest request = SupportRequest.of(PROJECT_ID, SOURCE, LOCATION, RESPONSE_TIME, DURATION, String.valueOf(i))
                    .withId(ObjectId.get());
            list.add(request);
            requestRepository.saveAll(list);
        }
        when(requestRepository.findByProjectId(eq(PROJECT_ID), any())).thenReturn(list);

        this.mockMvc.perform(get("/v1/requests/")
                .param("offset", "1")
                .param("limit", "10")
                .param("projectId", PROJECT_ID))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("data.requests.size()", is(9)));

    }

    @Test
    public void getBadRequestList() throws Exception {
        this.mockMvc.perform(get("/v1/requests/")
                .param("offset", "1")
                .param("limit", "10"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }



        @Test
        public void createSupportRequest() throws Exception {
            SupportRequest request = mock(SupportRequest.class);
            when(request.getId()).thenReturn(ObjectId.get().toString());

            when(requestRepository.save(any())).thenReturn(request);

            this.mockMvc.perform(post("/v1/requests/")
                    .content(CREATE_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("data.content.id", not(isEmptyOrNullString())));
        }

        @Test
        public void testUpdateSupportRequest() throws Exception {
            when(requestRepository.updateRequest(any(), any())).thenReturn(true);
            this.mockMvc.perform(put("/v1/requests/{0}", ObjectId.get().toString())
                    .content(CREATE_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
            verify(requestRepository, times(1)).updateRequest(any(), any());

        }

        @Test
        public void assignSupportRequest() throws Exception {
            ArgumentCaptor<Update> captor = ArgumentCaptor.forClass(Update.class);
            when(requestRepository.updateRequest(any(), captor.capture())).thenReturn(true);
            this.mockMvc.perform(put("/v1/requests/{0}", ObjectId.get().toString())
                    .content(ASSIGN_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
            verify(requestRepository, times(1)).updateRequest(any(), any());
            Assert.assertTrue(captor.getValue().modifies("inetti"));
        }


}