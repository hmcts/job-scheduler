package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.DateBuilder;
import org.quartz.TriggerBuilder;
import uk.gov.hmcts.reform.jobscheduler.model.Trigger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.quartz.TriggerBuilder.newTrigger;

final class TriggerConverter {

    private TriggerConverter() {
    }

    static org.quartz.Trigger toQuartzTrigger(final Trigger trigger) {
        TriggerBuilder<org.quartz.Trigger> triggerBuilder = newTrigger()
            .startAt(Date.from(trigger.startDateTime.toInstant()));

        if (trigger.interval != null && trigger.frequency != null) {
            triggerBuilder.withSchedule(CalendarIntervalScheduleBuilder
                .calendarIntervalSchedule()
                .withInterval(
                    trigger.interval,
                    DateBuilder.IntervalUnit.valueOf(trigger.frequency.name())
                )
            );
        }

        return triggerBuilder.build();
    }

    static Trigger toPlatformTrigger(org.quartz.Trigger trigger) {
        Trigger.Frequency frequency = null;
        Integer interval = null;

        // otherwise it is a SimpleTrigger and only has default interval defined in library
        if (trigger instanceof CalendarIntervalTrigger) {
            CalendarIntervalTrigger calendarTrigger = (CalendarIntervalTrigger) trigger;

            frequency = Trigger.Frequency.valueOf(calendarTrigger.getRepeatIntervalUnit().name());
            interval = calendarTrigger.getRepeatInterval();
        }

        return new Trigger(
            frequency,
            interval,
            ZonedDateTime.ofInstant(trigger.getStartTime().toInstant(), ZoneId.systemDefault())
        );
    }
}
