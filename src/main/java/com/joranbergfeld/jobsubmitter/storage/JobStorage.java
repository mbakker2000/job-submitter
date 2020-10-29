package com.joranbergfeld.jobsubmitter.storage;

import com.joranbergfeld.jobsubmitter.domain.JobStatus;
import java.util.List;
import java.util.concurrent.Future;

public interface JobStorage {

    JobStatus queryStatus(final String jobId);

    List<JobStatus> getAll();

    JobStatus addJob(final String jobId, Future<?> future);

    JobStatus cancelJob(final String jobId);
}
