package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.quartz.SchedulerException;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobException;

class GetterFromScheduler {

    @FunctionalInterface
    interface Getter<T, R> {
        R apply(T t) throws SchedulerException;
    }

    private GetterFromScheduler() {
    }

    static <T, R> R getFromScheduler(Getter<T, R> getter, T arg, String message) {
        try {
            return getter.apply(arg);
        } catch (SchedulerException exc) {
            throw new JobException(message, exc);
        }
    }

    static <T, R> R getFromScheduler(Getter<T, R> getter, T arg) {
        return getFromScheduler(getter, arg, "Error while getting information from scheduler");
    }
}
