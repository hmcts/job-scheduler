package uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions;

import uk.gov.hmcts.reform.logging.exception.AlertLevel;
import uk.gov.hmcts.reform.logging.exception.UnknownErrorCodeException;

public class JobActionSerializationException extends UnknownErrorCodeException {

    public JobActionSerializationException(String message, Throwable cause) {
        super(AlertLevel.P2, message, cause);
    }
}
