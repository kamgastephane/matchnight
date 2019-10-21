package com.inetti.matchnight.data.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class SupportRequestTest {
    @Test
    public void testEquals() {
        EqualsVerifier.forClass(SupportRequest.class).usingGetClass().verify();
    }
}