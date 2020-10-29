package com.joranbergfeld.jobsubmitter.web;


import com.joranbergfeld.jobsubmitter.domain.JobService;
import com.joranbergfeld.jobsubmitter.domain.JobStatus;
import com.joranbergfeld.jobsubmitter.domain.creator.JobCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobController {

    private final JobCreator jobCreator;
    private final JobService jobService;

    public JobController(JobCreator jobCreator, JobService jobService) {
        this.jobCreator = jobCreator;
        this.jobService = jobService;
    }

    @Operation(summary = "Queries a single job by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the job, will return status.",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = JobStatusResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Could not find the job.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JobStatusResponse> getById(@PathVariable("id") final String jobId) {
        return new ResponseEntity<>(JobStatus.toResponse(jobService.queryJob(jobId)), HttpStatus.OK);
    }

    @Operation(summary = "Queries all jobs.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the job, will return status.",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = JobStatusResponse[].class))}),
    })
    @GetMapping
    public ResponseEntity<List<JobStatusResponse>> getAll() {
        List<JobStatusResponse> jobStatusResponses = jobService.getAll()
            .stream()
            .map(JobStatus::toResponse)
            .collect(Collectors.toList());
        return new ResponseEntity<>(jobStatusResponses, HttpStatus.OK);
    }

    @Operation(summary = "Creates a job.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created and submitted the job, will return status.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = JobStatusResponse.class))})
    })
    @PostMapping
    public ResponseEntity<JobStatusResponse> createJob(@RequestBody CreateJobRequest request) {
        Runnable job = jobCreator.createJob(request.getJobTime());
        return new ResponseEntity<>(JobStatus.toResponse(jobService.submitJob(job)), HttpStatus.CREATED);
    }

    @Operation(summary = "Cancels the specified job.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancelled the job, will return status. "
            + "Note that this may not mean that the job is in cancelled state.",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = JobStatusResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Could not find the job.")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<JobStatusResponse> cancelJob(@PathVariable("id") final String jobId) {
        JobStatus jobStatus = jobService.cancelJob(jobId);
        return new ResponseEntity<>(JobStatus.toResponse(jobStatus), HttpStatus.OK);
    }
}
