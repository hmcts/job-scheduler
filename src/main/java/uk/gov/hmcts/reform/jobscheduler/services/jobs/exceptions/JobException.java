package uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions;

import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import static uk.gov.hmcts.reform.jobscheduler.logging.ErrorCode.UNKNOWN;

public class JobException extends AbstractLoggingException {

    public JobException(String message, Throwable cause) {
        super(AlertLevel.P1, UNKNOWN, message, cause);
    }
}
