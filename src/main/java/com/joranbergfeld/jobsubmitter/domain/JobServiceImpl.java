package com.joranbergfeld.jobsubmitter.domain;

import com.joranbergfeld.jobsubmitter.exceptions.JobSubmitterException;
import com.joranbergfeld.jobsubmitter.storage.JobStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.slf4j.Logger;

public class JobServiceImpl implements JobService {

    private final Logger logger;
    private final ExecutorService executorService;
    private final JobStorage jobStorage;

    public JobServiceImpl(Logger logger, ExecutorService executorService,
        JobStorage jobStorage) {
        this.logger = logger;
        this.executorService = executorService;
        this.jobStorage = jobStorage;
    }

    @Override
    public JobStatus submitJob(Runnable jobToSubmit) {
        Future<?> submit = executorService.submit(jobToSubmit);
        return jobStorage.addJob(generateJobId(), submit);
    }

    @Override
    public JobStatus cancelJob(String jobId) {
        return jobStorage.cancelJob(jobId);
    }

    @Override
    public JobStatus queryJob(final String jobId) {
        Optional<JobStatus> jobStatus = Optional.ofNullable(jobStorage.queryStatus(jobId));
        return jobStatus.orElseThrow(JobSubmitterException::new);
    }

    @Override
    public List<JobStatus> getAll() {
        return jobStorage.getAll();
    }

    private String generateJobId() {
        return UUID.randomUUID().toString();
    }
}
