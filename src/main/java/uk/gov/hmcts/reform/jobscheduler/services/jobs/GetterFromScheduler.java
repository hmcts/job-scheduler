package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.quartz.SchedulerException;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobException;

public class GetterFromScheduler {

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws SchedulerException;
    }

    private GetterFromScheduler() {
    }

    public static <T> T call(ThrowingSupplier<T> throwingSupplier) {
        try {
            return throwingSupplier.get();
        } catch (SchedulerException exc) {
            throw new JobException(exc.getMessage(), exc);
        }
    }
}
