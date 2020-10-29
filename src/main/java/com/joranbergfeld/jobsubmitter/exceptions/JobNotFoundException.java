package com.joranbergfeld.jobsubmitter.exceptions;

public class JobNotFoundException extends JobSubmitterException {

    private static final String MESSAGE = "Could not find job.";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
