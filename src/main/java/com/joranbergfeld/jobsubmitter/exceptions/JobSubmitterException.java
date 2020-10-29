package com.joranbergfeld.jobsubmitter.exceptions;

public class JobSubmitterException extends RuntimeException {

    private static final String MESSAGE = "Something went wrong.";

    public JobSubmitterException() {
        super();
    }

    public JobSubmitterException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
