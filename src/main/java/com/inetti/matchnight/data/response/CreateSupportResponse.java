package com.inetti.matchnight.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CreateSupportResponse {

    @JsonProperty("id")
    private final String supportRequestId;

    public CreateSupportResponse(@NotNull String supportRequestId) {
        this.supportRequestId = Objects.requireNonNull(supportRequestId, "cannot create a support request creation response without a valid id");
    }

    @JsonProperty("id")
    public String getSupportRequestId() {
        return supportRequestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateSupportResponse that = (CreateSupportResponse) o;
        return Objects.equals(supportRequestId, that.supportRequestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supportRequestId);
    }

    @Override
    public String toString() {
        return "CreateSupportResponse{" +
                "supportRequestId='" + supportRequestId + '\'' +
                '}';
    }
}
