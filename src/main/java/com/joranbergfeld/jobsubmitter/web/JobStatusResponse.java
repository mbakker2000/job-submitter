package com.joranbergfeld.jobsubmitter.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.StringJoiner;

public class JobStatusResponse {

    private final String id;
    private final String status;

    @JsonCreator
    public JobStatusResponse(@JsonProperty("id") final String id, @JsonProperty("status") final String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JobStatusResponse.class.getSimpleName() + "[", "]")
            .add("id='" + id + "'")
            .add("status='" + status + "'")
            .toString();
    }
}
