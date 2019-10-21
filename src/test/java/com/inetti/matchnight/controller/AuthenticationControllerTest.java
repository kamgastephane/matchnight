package com.inetti.matchnight.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inetti.matchnight.IntegrationTest;
import com.inetti.matchnight.configuration.JWTConfig;
import com.inetti.matchnight.data.dto.MatchnightRole;
import com.inetti.matchnight.data.model.Inetto;
import com.inetti.matchnight.data.repository.InettoRepository;
import com.inetti.matchnight.service.InettoServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationControllerTest extends IntegrationTest {

    private TestRestTemplate restTemplate;

    @Autowired
    private InettoServiceImpl service;

    @Autowired
    private InettoRepository repository;

    @Before
    public void setUp() throws Exception {
        restTemplate = new TestRestTemplate();
        repository.deleteAll();
        repository.purgeCache();
    }

    @Test
    public void testAuthentication() {
        initUser();
        User user = new User("stephane", "password");
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(createURLWithPort("/v1/login"), user, String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertTrue(responseEntity.getHeaders().containsKey(HttpHeaders.AUTHORIZATION));
        Assert.assertNotNull(responseEntity.getHeaders().get(HttpHeaders.AUTHORIZATION));
        Assert.assertTrue(responseEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).startsWith(JWTConfig.BEARER_PREFIX));
    }

    @Test
    public void testWrongCredentials() {
        User user = new User("stephane", "password1");
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(createURLWithPort("/v1/login"), user, String.class);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    public void testNotoken() {
        Map<String, String> param = new HashMap<>();
        param.put("year", "2019");
        param.put("month", "12");
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(createURLWithPort("/v1/events/match"), String.class, param);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    public void testWithToken() {
        initUser();
        Map<String, String> param = new HashMap<>();
        param.put("year", "2019");
        param.put("month", "12");
        User user = new User("stephane", "password");
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(createURLWithPort("/v1/login"), user, String.class);
        String header = responseEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        Assert.assertNotNull(header);
        MultiValueMap<String, String> headers = new HttpHeaders();

        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + header);
        HttpEntity<String> entity = new HttpEntity<String>("", headers);

        final ResponseEntity<String> exchange = restTemplate.exchange(createURLWithPort("/v1/events/match?month={month}&year={year}"), HttpMethod.GET, entity, String.class,
                "12", "2019");
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    private void initUser() {
        final Inetto inetto = new Inetto.InettoBuilder().withUsername("stephane").withPassword("password").withRole(MatchnightRole.ADMIN).build();
        service.createInetto(inetto);
    }

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
        repository.purgeCache();
    }

    private static class User {
        public final String username;
        public final String password;
        @JsonCreator
        public User(@JsonProperty("username") String username, @JsonProperty("password")String password) {
            this.username = username;
            this.password = password;
        }

    }
}
