package uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions;

import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.AlertLevel;

import static uk.gov.hmcts.reform.jobscheduler.logging.ErrorCode.UNKNOWN;

public class JobNotFoundException extends AbstractLoggingException {

    public JobNotFoundException() {
        super(AlertLevel.P4, UNKNOWN, (Throwable) null);
    }
}
