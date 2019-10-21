package com.inetti.matchnight.data.model;

import com.inetti.matchnight.data.dto.InettoContract;
import com.inetti.matchnight.data.dto.MatchnightRole;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * this class represent a matchnight user in the system
 */
@Document(collection = "inetto")
@TypeAlias("inetto")
public class Inetto implements InettoContract {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";
    public static final String ROLE = "role";
    public static final String CONTACT = "contact";
    public static final String METADATA = "metadata";

    @Id
    private final ObjectId id;

    @Indexed
    @Field(USERNAME)
    private final String username;

    @Field(PASSWORD)
    private final String password;

    @Field(FIRSTNAME)
    private final String firstName;

    @Field(LASTNAME)
    private final String lastName;

    @Field(ROLE)
    private final MatchnightRole role;

    @Field(CONTACT)
    private final Map<String, String> contacts;

    @Field(METADATA)
    private final Map<String, Object> metadata;

    @Version
    private final Long version;

    @PersistenceConstructor
    public Inetto(ObjectId id, String username, String password, String firstName,
                  String lastName, MatchnightRole role, Map<String, String> contacts, Map<String, Object> metadata, Long version) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.contacts = contacts;
        this.metadata = metadata;
        this.version = version;
    }

    /**
     * method needed by the mongo driver
     * @param id the id of the inetto
     * @return a new instance with the fields copied
     */
    public Inetto withId(ObjectId id) {
        return new Inetto(id, this.username, this.password, this.firstName, this.lastName, this.role, this.contacts, this.metadata, this.version);
    }

    /**
     * Method need to overwrite the password
     * @param password the password encoded
     * @return a new instance with the password field copied
     */
    public Inetto withPassword(String password) {
        return new Inetto(this.id, this.username, password, this.firstName, this.lastName, this.role, this.contacts, this.metadata, this.version);
    }
    /**
     * method needed by the mongo driver
     * @param version the version of the inetto
     * @return a new instance with the fields copied
     */
    public Inetto withVersion(Long version) {
        return new Inetto(this.id, this.username, this.password, this.firstName, this.lastName, this.role, this.contacts, this.metadata, version);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Optional.ofNullable(role)
                .map(MatchnightRole::getAll)
                .orElse(Collections.emptyList())
                .stream()
                .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    }

    public ObjectId getRepositoryId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public MatchnightRole getRole() {
        return role;
    }

    @Override
    public String getId() {
        return id.toString();
    }

    public Instant getCreationDate() {
        return Optional.ofNullable(id).map(objectId -> Instant.ofEpochSecond(objectId.getTimestamp())).orElse(null);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public Map<String, String> getContacts() {
        return contacts;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inetto inetto = (Inetto) o;
        return Objects.equals(id, inetto.id) &&
                Objects.equals(username, inetto.username) &&
                Objects.equals(password, inetto.password) &&
                Objects.equals(firstName, inetto.firstName) &&
                Objects.equals(lastName, inetto.lastName) &&
                role == inetto.role &&
                Objects.equals(contacts, inetto.contacts) &&
                Objects.equals(metadata, inetto.metadata) &&
                Objects.equals(version, inetto.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, firstName, lastName, role, contacts, metadata, version);
    }

    @Override
    public String toString() {
        return "Inetto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", contacts=" + contacts +
                ", metadata=" + metadata +
                ", version=" + version +
                '}';
    }

    public static final class InettoBuilder {
        private ObjectId id;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private MatchnightRole role;
        private Map<String, String> contacts;
        private Map<String, Object> metadata;

        public InettoBuilder() {
            contacts = new HashMap<>();
            metadata = new HashMap<>();
        }

        public InettoBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public InettoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public InettoBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public InettoBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public InettoBuilder withContacts(Map<String, String> contacts) {
            this.contacts = contacts;
            return this;
        }

        public InettoBuilder withMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public InettoBuilder withContacts(String key, String value) {
            this.contacts.put(key, value);
            return this;
        }

        public InettoBuilder withMetadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public InettoBuilder withRole(MatchnightRole role) {
            this.role = role;
            return this;
        }

        public Inetto build() {
            return new Inetto(id, username, password, firstName, lastName,
                    role,
                    contacts.isEmpty() ? null: contacts,
                    metadata.isEmpty() ? null: metadata,
                    null);
        }
    }
}
