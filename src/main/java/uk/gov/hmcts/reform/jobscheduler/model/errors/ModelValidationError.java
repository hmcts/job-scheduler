package uk.gov.hmcts.reform.jobscheduler.model.errors;

import java.util.List;

public class ModelValidationError {

    public final List<FieldError> errors;

    public ModelValidationError(List<FieldError> errors) {
        this.errors = errors;
    }
}
