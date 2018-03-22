package uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions;

import uk.gov.hmcts.reform.logging.exception.AlertLevel;
import uk.gov.hmcts.reform.logging.exception.UnknownErrorCodeException;

public class JobNotFoundException extends UnknownErrorCodeException {

    public JobNotFoundException() {
        super(AlertLevel.P4, (Throwable) null);
    }
}
