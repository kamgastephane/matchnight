package com.inetti.matchnight.service;

import com.inetti.matchnight.MockMVCTest;
import com.inetti.matchnight.data.repository.InettoRepository;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class InettoServiceImplTest extends MockMVCTest {

    @Autowired
    private InettoRepository repository;

    @Autowired
    private List<InettoService> services;


    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFindById() {
        //todo
    }

    @Test
    public void testFindByUsername() {
        //todo
    }

    @Test
    public void testCreate() {
        //todo
    }

    @Test
    public void testSearch() {
        //todo
    }


}