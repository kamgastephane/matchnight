package com.inetti.matchnight.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * Represent a simple request of support for a match night
 */
@Document(collection = "requests")
@TypeAlias("requests")
public class SupportRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportRequest.class);

    public static final String PROJECT_ID = "projectId";
    public static final String REQUEST_SOURCE = "requestSource";
    public static final String LOCATION = "location";
    public static final String RESPONSE_TIME = "responseTime";
    public static final String DURATION = "duration";
    public static final String EVENT_ID = "eventId";
    public static final String ARCHIVED = "archived";

    private static final HashMap<String, Duration> DURATION_MAP = new HashMap<>(4);
    private static final HashMap<Integer, Location> LOCATION_MAP = new HashMap<>(2);
    private static final HashMap<Integer, ResponseTime> RESPONSE_TIME_MAP = new HashMap<>(4);

    static {
        Arrays.stream(Duration.values()).forEach(supportDuration -> DURATION_MAP.put(supportDuration.code, supportDuration));
        Arrays.stream(Location.values()).forEach(location -> LOCATION_MAP.put(location.id, location));
        Arrays.stream(ResponseTime.values()).forEach(responseTime -> RESPONSE_TIME_MAP.put(responseTime.id, responseTime));
    }


    @Id
    private final ObjectId id;

    @Field(PROJECT_ID)
    private final String projectId;

    @Field(REQUEST_SOURCE)
    private final String requestSource;

    @Field(LOCATION)
    private final Location where;

    @Field(RESPONSE_TIME)
    private final ResponseTime responseTime;

    @Field(DURATION)
    private final Duration duration;

    @Field(EVENT_ID)
    private final String eventId;

    @Version
    private final Long version;

    @Field(ARCHIVED)
    private final Boolean archived;


    private SupportRequest(ObjectId id, String projectId, String requestSource, Location where, ResponseTime responseTime,
                           Duration duration, Long version, String eventId, Boolean archived) {

        this.id = id;
        this.projectId = projectId;
        this.requestSource = requestSource;
        this.where = where;
        this.responseTime = responseTime;
        this.duration = duration;
        this.version = version;
        this.eventId = eventId;
        this.archived = archived;
    }

    @JsonCreator
    public static SupportRequest of(@JsonProperty("projectId") String projectId,
                                    @JsonProperty("requestSource") String requestSource,
                                    @JsonProperty("where") Location location,
                                    @JsonProperty("responseTime") ResponseTime responseTime,
                                    @JsonProperty("duration") Duration duration,
                                    @JsonProperty("eventId") String eventId) {

        Objects.requireNonNull(projectId, "projectId cannot be null while creating a request");
        Objects.requireNonNull(location, "location cannot be null while creating a request");
        Objects.requireNonNull(responseTime, "responseTime cannot be null while creating a request");
        Objects.requireNonNull(duration, "duration cannot be null while creating a request");

        return new SupportRequest(null, projectId, requestSource, location, responseTime, duration, null, eventId, false);
    }

    /**
     * method needed by the mongo driver
     * @param id the id of the request
     * @return a new instance with the fields copied
     */
    public SupportRequest withId(ObjectId id) {
        return new SupportRequest(id, this.projectId, this.requestSource, this.where, this.responseTime,
                this.duration, this.version, this.eventId, this.archived);
    }

    /**
     * method needed by the mongo driver
     * @param version this document version
     * @return a new instance with the fields copied
     */
    public SupportRequest withVersion(Long version) {
        return new SupportRequest(this.id, this.projectId, this.requestSource, this.where, this.responseTime,
                this.duration, version, this.eventId, this.archived);
    }

    public String getId() {
        return id.toString();
    }

    public String getProjectId() {
        return projectId;
    }

    public String getRequestSource() {
        return requestSource;
    }

    public Location getWhere() {
        return where;
    }

    public ResponseTime getResponseTime() {
        return responseTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public Long getVersion() {
        return version;
    }

    public String getEventId() {
        return eventId;
    }

    public Boolean getArchived() {
        return archived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupportRequest request = (SupportRequest) o;
        return Objects.equals(id, request.id) &&
                Objects.equals(projectId, request.projectId) &&
                Objects.equals(requestSource, request.requestSource) &&
                where == request.where &&
                responseTime == request.responseTime &&
                duration == request.duration &&
                Objects.equals(version, request.version) &&
                Objects.equals(archived, request.archived);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId, requestSource, where, responseTime, duration, version, archived);
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", projectId='" + projectId + '\'' +
                ", requestSource='" + requestSource + '\'' +
                ", where=" + where +
                ", responseTime=" + responseTime +
                ", duration=" + duration +
                ", version=" + version +
                ", archived=" + archived +
                '}';
    }

    /**
     * the "where" associated to this support request
     */
    public enum Location {
        ON_CALL(1, "on_call"),
        ON_SITE(2, "on_site");

        private Integer id;
        private String code;
        Location(int id, String code) {
            this.id = id;
            this.code = code;
        }

        @JsonCreator
        public  static Location of(Integer id) {
            return LOCATION_MAP.get(id);
        }

        @JsonValue
        public Integer getId() {
            return id;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * the response time associated with this support request
     */
    public enum ResponseTime {
        IMMEDIATE(0, "immediate"),
        ONE_HOUR(1, "one_hour"),
        TWO_HOURS(2, "two_hours"),
        BUSINESS_HOURS(3, "business_hours");

        private Integer id;
        private String code;

        ResponseTime(Integer id, String code) {
            this.id = id;
            this.code = code;
        }

        public static ResponseTime of(Integer id) {
            return RESPONSE_TIME_MAP.get(id);
        }

        public Integer getId() {
            return id;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Those are the standard duration code currently supported by Inetti
     */
    public static enum Duration {
        H4("h4", "less or equals to 4 hours"),
        H8("h8", "Between 4 and 8 hours"),
        H16("h16", "Between 8 and 16 hours"),
        H24("h24", "Between 16 and 24 hours");

        private final String code;
        private final String description;

        Duration(String code, String description) {
            this.code = code;
            this.description = description;
        }
        @JsonValue
        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
        @JsonCreator
        public static Duration withCode(String code) {
            return DURATION_MAP.get(Optional.ofNullable(code).map(String::toLowerCase).orElse(null));
        }
    }
}
