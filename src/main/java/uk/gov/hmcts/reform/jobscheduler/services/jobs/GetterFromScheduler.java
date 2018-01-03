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

    static <T> T call(ThrowingSupplier<T> supplier, String message) {
        try {
            return supplier.get();
        } catch (SchedulerException exc) {
            throw new JobException(message, exc);
        }
    }

    public static <T> T call(ThrowingSupplier<T> throwingSupplier) {
        return call(throwingSupplier, "Error while getting information from scheduler");
    }
}
