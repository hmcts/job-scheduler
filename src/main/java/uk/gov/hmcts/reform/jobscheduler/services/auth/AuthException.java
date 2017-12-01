package uk.gov.hmcts.reform.jobscheduler.services.auth;

public class AuthException extends RuntimeException {

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
