package com.joranbergfeld.jobsubmitter.domain;

import java.util.List;

public interface JobService {

    JobStatus queryJob(final String jobId);

    List<JobStatus> getAll();

    JobStatus submitJob(Runnable jobToSubmit);

    JobStatus cancelJob(final String jobId);
}
