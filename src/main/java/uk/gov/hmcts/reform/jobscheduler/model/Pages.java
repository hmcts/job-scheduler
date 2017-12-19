package uk.gov.hmcts.reform.jobscheduler.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Pages<T> extends PageImpl<T> {

    public Pages(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public Pages(List<T> content) {
        super(content);
    }
}
