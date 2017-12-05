package uk.gov.hmcts.reform.jobscheduler.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Singleton;

@Configuration
public class QuartzConfiguration {

    @Bean
    @Singleton
    public Scheduler scheduler() throws SchedulerException {
        // todo: use database as job store
        StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();

        scheduler.start();

        return scheduler;
    }
}
