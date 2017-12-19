package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.data.domain.Sort;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public final class PageRequest extends org.springframework.data.domain.PageRequest {

    @Deprecated
    private PageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, Sort.unsorted());
    }
}
