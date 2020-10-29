package com.joranbergfeld.jobsubmitter.exceptions;

public class JobFailedException extends JobSubmitterException {

    private static final String MESSAGE = "Job failed.";

    public JobFailedException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
