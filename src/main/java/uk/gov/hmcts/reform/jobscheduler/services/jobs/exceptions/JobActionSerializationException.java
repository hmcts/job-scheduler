package uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions;

import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import static uk.gov.hmcts.reform.jobscheduler.logging.ErrorCode.UNKNOWN;

public class JobActionSerializationException extends AbstractLoggingException {

    public JobActionSerializationException(String message, Throwable cause) {
        super(AlertLevel.P2, UNKNOWN, message, cause);
    }
}
