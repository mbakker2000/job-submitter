package com.joranbergfeld.jobsubmitter.domain;

import com.joranbergfeld.jobsubmitter.web.JobStatusResponse;
import java.util.StringJoiner;

public class JobStatus {

    private final String id;
    private final String status;

    public JobStatus(final String id, final String status) {
        this.id = id;
        this.status = status;
    }

    public static JobStatusResponse toResponse(JobStatus status) {
        if (status == null) {
            return null;
        }
        return new JobStatusResponse(status.getId(), status.getStatus());
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JobStatus.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("status='" + status + "'")
            .toString();
    }
}
