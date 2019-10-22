package com.inetti.matchnight.data.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inetti.matchnight.data.dto.MatchnightRole;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateInettoRequest {


    private final String username;
    private final MatchnightRole role;
    private final Map<String, String> contacts;
    private final Map<String, String> metadata;

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public CreateInettoRequest(@NotNull @JsonProperty("username") String username,
                               @NotNull @JsonProperty("role") MatchnightRole role,
                               @NotNull Map<String, String> contacts, Map<String, String> metadata) {
        this.username = username;
        this.role = role;
        this.contacts = contacts;
        this.metadata = metadata;
    }

    public String getUsername() {
        return username;
    }

    public MatchnightRole getRole() {
        return role;
    }

    public Map<String, String> getContacts() {
        return contacts;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateInettoRequest that = (CreateInettoRequest) o;
        return Objects.equals(username, that.username) &&
                role == that.role &&
                Objects.equals(contacts, that.contacts) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, role, contacts, metadata);
    }

    @Override
    public String toString() {
        return "CreateInettoRequest{" +
                "username='" + username + '\'' +
                ", role=" + role +
                ", contacts=" + contacts +
                ", metadata=" + metadata +
                '}';
    }
}
