package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.junit.Test;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import uk.gov.hmcts.reform.jobscheduler.model.Trigger;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.jobscheduler.SampleData.validJob;
import static uk.gov.hmcts.reform.jobscheduler.SampleData.validJobEmptyInterval;

public class TriggerConverterTest {

    @Test
    public void should_set_custom_scheduler_builder_and_recreate_the_trigger() {
        Trigger originalTrigger = validJob().trigger;
        org.quartz.Trigger trigger = TriggerConverter.toQuartzTrigger(originalTrigger);

        assertThat(trigger.getScheduleBuilder()).isOfAnyClassIn(CalendarIntervalScheduleBuilder.class);
        assert_that_rebuilt_trigger_is_same_as_original(trigger, originalTrigger);
    }

    @Test
    public void should_use_default_scheduler_builder_and_recreate_the_trigger() {
        Trigger originalTrigger = validJobEmptyInterval().trigger;
        org.quartz.Trigger trigger = TriggerConverter.toQuartzTrigger(originalTrigger);

        assertThat(trigger.getScheduleBuilder()).isOfAnyClassIn(SimpleScheduleBuilder.class);
        assert_that_rebuilt_trigger_is_same_as_original(trigger, originalTrigger);
    }

    private void assert_that_rebuilt_trigger_is_same_as_original(org.quartz.Trigger trigger, Trigger originalTrigger) {
        Trigger rebuiltTrigger = TriggerConverter.toPlatformTrigger(trigger);

        assertThat(rebuiltTrigger).isEqualToComparingFieldByFieldRecursively(originalTrigger);
    }
}
