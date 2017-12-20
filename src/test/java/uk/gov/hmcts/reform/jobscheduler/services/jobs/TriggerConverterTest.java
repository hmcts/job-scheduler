package uk.gov.hmcts.reform.jobscheduler.services.jobs;

import org.junit.Test;
import org.quartz.SimpleScheduleBuilder;
import uk.gov.hmcts.reform.jobscheduler.model.Trigger;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.jobscheduler.SampleData.validJob;

public class TriggerConverterTest {

    @Test
    public void should_use_default_scheduler_builder_and_convert_triggers_correctly() {
        Trigger originalTrigger = validJob().trigger;
        org.quartz.Trigger trigger = TriggerConverter.toQuartzTrigger(originalTrigger);

        // dummy assertion to remind in the future of different schedulers in use and verify
        assertThat(trigger.getScheduleBuilder()).isOfAnyClassIn(SimpleScheduleBuilder.class);
        assertThat(trigger.getJobDataMap().getIntValue(JobDataKeys.ATTEMPT)).isEqualTo(1);

        Trigger rebuiltTrigger = TriggerConverter.toPlatformTrigger(trigger);

        assertThat(rebuiltTrigger).isEqualToComparingFieldByFieldRecursively(originalTrigger);
    }
}
