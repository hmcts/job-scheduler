package uk.gov.hmcts.reform.jobscheduler.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.model.JobList;
import uk.gov.hmcts.reform.jobscheduler.services.auth.AuthService;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.JobsService;

import java.net.URI;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@Validated
@RequestMapping(path = "/jobs")
public class JobsController {

    private final JobsService jobsService;
    private final AuthService authService;

    public JobsController(JobsService jobsService, AuthService authService) {
        this.jobsService = jobsService;
        this.authService = authService;
    }

    @PostMapping(path = "")
    @ApiOperation("Create a new job")
    public ResponseEntity<Void> create(
        @Valid @RequestBody Job job,
        @RequestHeader("ServiceAuthorization") String serviceAuthHeader
    ) {
        String serviceName = authService.authenticate(serviceAuthHeader);
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
        String serviceName = authService.authenticate(serviceAuthHeader);
        jobsService.delete(id, serviceName);

        return noContent().build();
    }

    @GetMapping(path = "")
    @ApiOperation("Get list of jobs for a service")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success")
    })
    public JobList getAll(
        @RequestHeader("ServiceAuthorization") String serviceAuthHeader
    ) {
        String serviceName = authService.authenticate(serviceAuthHeader);

        return jobsService.getAll(serviceName);
    }
}
