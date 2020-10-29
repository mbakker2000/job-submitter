package com.joranbergfeld.jobsubmitter.domain.creator;

public interface JobCreator {

    Runnable createJob(long jobTime);
}
