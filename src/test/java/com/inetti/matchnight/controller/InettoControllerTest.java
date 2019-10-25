package com.inetti.matchnight.controller;

import com.inetti.matchnight.MockMVCTest;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class InettoControllerTest extends MockMVCTest {

    @Test
    public void testCreate() {
        //todo
    }

    @Test
    public void testSearchInvalid() throws Exception {
        this.mockMvc.perform(get("/v1/user?" + "qs" ))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}