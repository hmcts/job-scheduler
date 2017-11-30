package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class Trigger {

    public final Frequency frequency;
    public final Integer interval;
    public final ZonedDateTime startDateTime;

    public Trigger(
        @JsonProperty("frequency") Frequency frequency,
        @JsonProperty("interval") Integer interval,
        @JsonProperty("start_date_time") ZonedDateTime startDateTime
    ) {
        this.frequency = frequency;
        this.interval = interval;
        this.startDateTime = startDateTime;
    }

    public enum Frequency {
        @JsonProperty("second") SECOND,
        @JsonProperty("minute") MINUTE,
        @JsonProperty("hour") HOUR,
        @JsonProperty("day") DAY,
        @JsonProperty("week") WEEK,
        @JsonProperty("month") MONTH
    }
}
