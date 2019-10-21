package com.inetti.matchnight.data.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inetti.matchnight.data.model.SupportRequest;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Validated
public class CreateSupportRequest extends UpdateSupportRequest{

    @JsonProperty(SupportRequest.PROJECT_ID)
    @NotNull
    private final String projectId;

    @JsonCreator
    public CreateSupportRequest(@NotNull String projectId, @NotNull SupportRequest.Location location, @NotNull SupportRequest.Duration duration,
                                @NotNull SupportRequest.ResponseTime responseTime, String source, String eventId) {
        super(location, duration, responseTime, source, eventId, null);
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CreateSupportRequest request = (CreateSupportRequest) o;
        return Objects.equals(projectId, request.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), projectId);
    }

    @Override
    public String toString() {
        return "CreateSupportRequest{" +
                "projectId='" + projectId + '\'' +
                ", location=" + location +
                ", duration=" + duration +
                ", responseTime=" + responseTime +
                ", eventId='" + eventId + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
