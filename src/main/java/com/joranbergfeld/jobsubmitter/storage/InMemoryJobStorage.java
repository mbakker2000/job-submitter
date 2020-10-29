package com.joranbergfeld.jobsubmitter.storage;

import static com.joranbergfeld.jobsubmitter.domain.JobState.CANCELLED;
import static com.joranbergfeld.jobsubmitter.domain.JobState.FINISHED;
import static com.joranbergfeld.jobsubmitter.domain.JobState.IN_PROGRESS;

import com.joranbergfeld.jobsubmitter.domain.JobStatus;
import com.joranbergfeld.jobsubmitter.exceptions.JobNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.slf4j.Logger;

public class InMemoryJobStorage implements JobStorage {

    private final Map<String, Future<?>> storage = new HashMap<>();
    private final Logger logger;

    public InMemoryJobStorage(Logger logger) {
        this.logger = logger;
    }

    @Override
    public JobStatus addJob(String jobId, Future<?> future) {
        logger.debug("Submitting job with id: " + jobId);
        storage.put(jobId, future);
        String status = getStatusOfFuture(future);
        return new JobStatus(jobId, status);
    }

    @Override
    public JobStatus cancelJob(String jobId) {
        Optional<? extends Future<?>> future = Optional.ofNullable(storage.get(jobId));
        return future.map(f -> cancelFuture(f, jobId)).orElseThrow(JobNotFoundException::new);
    }

    @Override
    public JobStatus queryStatus(String jobId) {
        Optional<? extends Future<?>> future = Optional.ofNullable(storage.get(jobId));
        return future.map(f -> new JobStatus(jobId, getStatusOfFuture(f))).orElseThrow(JobNotFoundException::new);
    }

    @Override
    public List<JobStatus> getAll() {
        return storage.entrySet()
            .stream()
            .map(stringFutureEntry -> new JobStatus(stringFutureEntry.getKey(),
                getStatusOfFuture(stringFutureEntry.getValue())))
            .collect(Collectors.toList());
    }

    private JobStatus cancelFuture(Future<?> future, final String jobId) {
        boolean cancelled = future.cancel(true);
        if (!cancelled) {
            logger.debug("Could not cancel job. It's probably finished already.");
        }
        return new JobStatus(jobId, getStatusOfFuture(future));
    }

    private String getStatusOfFuture(Future<?> future) {
        if (future.isCancelled()) {
            return CANCELLED;
        } else if (future.isDone()) {
            return FINISHED;
        } else {
            return IN_PROGRESS;
        }
    }
}
