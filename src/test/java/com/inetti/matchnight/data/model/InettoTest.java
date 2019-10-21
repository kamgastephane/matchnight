package com.inetti.matchnight.data.model;

import com.inetti.matchnight.data.dto.MatchnightAuthority;
import com.inetti.matchnight.data.dto.MatchnightRole;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class InettoTest {

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(Inetto.class).usingGetClass().verify();
    }

    @Test
    public void testRole() {
        Inetto inetto = new Inetto.InettoBuilder()
                .withFirstName("firstname")
                .withLastName("lastname")
                .withUsername("username")
                .withPassword("password")
                .withRole(MatchnightRole.PUBLISHER)
                .withContacts("email", "email")
                .build();
        Assert.assertEquals(2, inetto.getAuthorities().size());
        List<String> authorities = inetto.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Assert.assertTrue(authorities.contains(MatchnightRole.PUBLISHER.getName()));
        Assert.assertTrue(authorities.contains(MatchnightAuthority.CAN_PUBLISH_AUTHORITY));

    }
}