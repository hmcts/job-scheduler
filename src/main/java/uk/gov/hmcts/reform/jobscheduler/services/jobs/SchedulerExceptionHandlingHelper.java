package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.quartz.SchedulerException;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobException;

public class SchedulerExceptionHandlingHelper {

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws SchedulerException;
    }

    public static <T> T call(ThrowingSupplier<T> throwingSupplier) {
        try {
            return throwingSupplier.get();
        } catch (SchedulerException exc) {
            throw new JobException(exc.getMessage(), exc);
        }
    }

    private SchedulerExceptionHandlingHelper() {
    }
}
