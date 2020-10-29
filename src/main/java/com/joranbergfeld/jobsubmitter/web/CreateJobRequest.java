package com.joranbergfeld.jobsubmitter.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.StringJoiner;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateJobRequest {

    @NotNull
    @Min(0)
    @Max(Long.MAX_VALUE)
    private final long jobTime;

    @JsonCreator
    public CreateJobRequest(@JsonProperty("jobTime") long jobTime) {
        this.jobTime = jobTime;
    }

    public long getJobTime() {
        return jobTime;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateJobRequest.class.getSimpleName() + "[", "]")
            .add("jobTime=" + jobTime)
            .toString();
    }
}
