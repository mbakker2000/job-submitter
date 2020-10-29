package com.joranbergfeld.jobsubmitter.domain.creator;

import com.joranbergfeld.jobsubmitter.exceptions.JobFailedException;

public class JobCreatorImpl implements JobCreator {

    @Override
    public Runnable createJob(long jobTime) {
        return () -> {
            try {
                Thread.sleep(jobTime);
            } catch (InterruptedException e) {
                throw new JobFailedException(e);
            }
        };
    }
}
