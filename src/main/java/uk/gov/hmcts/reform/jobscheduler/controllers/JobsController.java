package uk.gov.hmcts.reform.jobscheduler.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.model.JobData;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;
import uk.gov.hmcts.reform.jobscheduler.services.s2s.S2sClient;

import java.net.URI;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@Validated
@RequestMapping(path = "/jobs")
public class JobsController {

    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 100;

    private final JobsService jobsService;
    private final S2sClient authTokenValidator;

    public JobsController(JobsService jobsService, S2sClient authTokenValidator) {
        this.jobsService = jobsService;
        this.authTokenValidator = authTokenValidator;
    }

    @PostMapping(path = "")
    @ApiOperation("Create a new job")
    public ResponseEntity<Void> create(
        @Valid @RequestBody Job job,
        @RequestHeader("ServiceAuthorization") String serviceAuthHeader
    ) {
        String serviceName = authTokenValidator.getServiceName(serviceAuthHeader);
        String id = this.jobsService.create(job, serviceName);

        URI newJobUri = fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

        return created(newJobUri).build();
    }

    @DeleteMapping(path = "/{id}")
    @ApiOperation("Delete job by id")
    public ResponseEntity<Void> delete(
        @PathVariable("id") String id,
        @RequestHeader("ServiceAuthorization") String serviceAuthHeader
    ) {
        String serviceName = authTokenValidator.getServiceName(serviceAuthHeader);
        jobsService.delete(id, serviceName);

        return noContent().build();
    }

    @GetMapping(path = "")
    @ApiOperation("Get list of jobs for a service")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success")
    })
    public Page<JobData> getAll(@RequestHeader("ServiceAuthorization")
                                    String serviceAuthHeader,
                                @Min(0)
                                @RequestParam(value = "page", defaultValue = "0")
                                    int page,
                                @Min(MIN_PAGE_SIZE)
                                @Max(MAX_PAGE_SIZE)
                                @RequestParam(value = "size", defaultValue = "10")
                                    int size
    ) {
        String serviceName = authTokenValidator.getServiceName(serviceAuthHeader);

        return jobsService.getAll(serviceName, page, size);
    }
}
