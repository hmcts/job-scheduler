package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import uk.gov.hmcts.reform.jobscheduler.model.Trigger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.quartz.TriggerBuilder.newTrigger;

final class TriggerConverter {

    private TriggerConverter() {
        // static class constructor
    }

    static org.quartz.Trigger toQuartzTrigger(final Trigger trigger) {
        return newTrigger()
            .startAt(Date.from(trigger.startDateTime.toInstant()))
            .usingJobData(JobDataKeys.ATTEMPT, 1)
            .build();
    }

    static Trigger toPlatformTrigger(org.quartz.Trigger trigger) {
        return new Trigger(
            ZonedDateTime.ofInstant(trigger.getStartTime().toInstant(), ZoneId.systemDefault())
        );
    }
}
