package com.inetti.matchnight.data.repository;

import com.inetti.matchnight.MockMVCTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.Assert.*;

public class MatchEventRepositoryImplTest extends MockMVCTest {

    @Autowired
    private RequestRepository repository;

    @Autowired
    private RedisTemplate<String, Object> template;

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void testSave() {
        //todo
    }


    @Test
    public void testReadFromCache() {
        //todo

    }

    @Test
    public void testCachekey() {
        //todo

    }

    @Test
    public void testCachePurge() {
        //todo

    }

}