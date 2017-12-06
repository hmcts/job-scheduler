package uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions;

public class JobActionSerializationException extends RuntimeException {

    public JobActionSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
