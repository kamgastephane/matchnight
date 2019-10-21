package com.inetti.matchnight.data.dto;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface InettoContract extends UserDetails {

    static final String ROLE_KEY = "role";


    public String getId();

    public String getUsername();

    public String getPassword();

    public String getFirstName();

    public String getLastName();

    public Map<String, String> getContacts();

    public Map<String, Object> getMetadata();
}
