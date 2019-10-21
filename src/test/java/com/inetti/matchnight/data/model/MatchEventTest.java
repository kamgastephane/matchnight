package com.inetti.matchnight.data.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class MatchEventTest {

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(MatchEvent.class).usingGetClass().verify();
    }
}