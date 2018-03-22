package uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions;

import uk.gov.hmcts.reform.logging.exception.AlertLevel;
import uk.gov.hmcts.reform.logging.exception.UnknownErrorCodeException;

public class JobException extends UnknownErrorCodeException {

    public JobException(String message, Throwable cause) {
        super(AlertLevel.P1, message, cause);
    }
}
