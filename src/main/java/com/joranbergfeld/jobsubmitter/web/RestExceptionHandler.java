package com.joranbergfeld.jobsubmitter.web;

import com.joranbergfeld.jobsubmitter.exceptions.JobNotFoundException;
import com.joranbergfeld.jobsubmitter.exceptions.JobSubmitterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(JobSubmitterException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity handleRootException(JobSubmitterException exception) {
        logger.error("Something went very wrong. Message: " + exception.getMessage(), exception);
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity handleNotFoundException(JobNotFoundException exception) {
        logger.warn("Could not find a job. This may, or may not, be an issue. Message: " + exception.getMessage(),
            exception);
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
