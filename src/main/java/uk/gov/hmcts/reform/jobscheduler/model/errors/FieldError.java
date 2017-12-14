package uk.gov.hmcts.reform.jobscheduler.model.errors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FieldError {

    @JsonProperty("field_name")
    public final String fieldName;

    public final String value;

    public FieldError(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }
}
