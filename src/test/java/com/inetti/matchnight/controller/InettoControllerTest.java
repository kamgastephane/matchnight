package com.inetti.matchnight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inetti.matchnight.MockMVCTest;
import com.inetti.matchnight.data.dto.MatchnightRole;
import com.inetti.matchnight.data.repository.InettoRepository;
import com.inetti.matchnight.data.request.CreateInettoRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class InettoControllerTest extends MockMVCTest {


    private ObjectMapper mapper;

    @Autowired
    private InettoRepository repository;

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }


    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        repository.deleteAll();

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreate() throws Exception {
        CreateInettoRequest request = new CreateInettoRequest("publisher", MatchnightRole.PUBLISHER, Collections.emptyMap(),
                Collections.emptyMap());

        this.mockMvc.perform(post("/v1/user").content(mapper.writeValueAsString(request)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    public void testSearchInvalid() throws Exception {
        this.mockMvc.perform(get("/v1/user?query=q").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser(username = "admin", password = "admin", roles = "USER")
    @Test
    public void testUnauthorized() throws Exception {
        CreateInettoRequest request = new CreateInettoRequest("publisher", MatchnightRole.PUBLISHER, Collections.emptyMap(),
                Collections.emptyMap());
        this.mockMvc.perform(post("/v1/user").content(mapper.writeValueAsString(request)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
