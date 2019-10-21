package com.inetti.matchnight.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum MatchnightRole {


    ADMIN("admin", MatchnightAuthority.ALL_AUTHORITY),
    USER("user"),
    PUBLISHER("publisher", MatchnightAuthority.CAN_PUBLISH_AUTHORITY);

    private final String name;

    private final List<String> authorities;

    private static final Map<String, MatchnightRole> matchnightroleMap = new HashMap<>();

    static {
        Arrays.stream(MatchnightRole.values()).forEach(role -> matchnightroleMap.put(role.name, role));
    }

    MatchnightRole(String name, String... authorities) {
        this.name = name;
        this.authorities = Arrays.asList(authorities);
    }

    public String getName() {
        return name;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public List<String> getAll() {
        List<String> result = new ArrayList<>(authorities);
        result.add(name);
        return result;
    }

    /**
     * @param role the name of a {@link MatchnightRole}
     * @return a {@link MatchnightRole}
     */
    @JsonCreator
    public static MatchnightRole of(String role) {
        return matchnightroleMap.get(role);
    }
}
