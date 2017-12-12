package uk.gov.hmcts.reform.jobscheduler.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.inject.Singleton;

@Configuration
@ConfigurationProperties
public class QuartzConfiguration {

    private final Map<String, String> quartzProperties = new HashMap<>();

    // this getter is needed by the framework
    public Map<String, String> getQuartzProperties() {
        return quartzProperties;
    }

    @Bean
    @Singleton
    public Scheduler scheduler() throws SchedulerException {
        Properties properties = new Properties();
        properties.putAll(quartzProperties);

        StdSchedulerFactory factory = new StdSchedulerFactory(properties);
        Scheduler scheduler = factory.getScheduler();

        scheduler.start();

        return scheduler;
    }
}
