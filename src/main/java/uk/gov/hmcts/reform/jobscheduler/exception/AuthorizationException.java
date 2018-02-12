package uk.gov.hmcts.reform.jobscheduler.exception;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException() {
        this("FORBIDDEN");
    }

    public AuthorizationException(String message) {
        super(message);
    }
}
