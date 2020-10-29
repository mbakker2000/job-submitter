package com.joranbergfeld.jobsubmitter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.joranbergfeld.jobsubmitter.domain.JobState;
import com.joranbergfeld.jobsubmitter.web.CreateJobRequest;
import com.joranbergfeld.jobsubmitter.web.JobStatusResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class JobCreationITTest {

    @LocalServerPort
    int port;

    @Test
    @DisplayName("Should be able to submit and query job. It should run to completion.")
    void shouldAcceptNewJobs() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        CreateJobRequest request = new CreateJobRequest(3000);
        ResponseEntity<JobStatusResponse> jobStatusResponseEntity = restTemplate
            .postForEntity("http://localhost:" + port + "/job", request, JobStatusResponse.class,
                Collections.emptyMap());

        assertEquals(201, jobStatusResponseEntity.getStatusCodeValue(), "Should return with HTTP OK.");
        assertNotNull(jobStatusResponseEntity.getBody(), "Should have a body returned.");

        JobStatusResponse initialStatus = jobStatusResponseEntity.getBody();
        assertEquals(JobState.IN_PROGRESS, initialStatus.getStatus(), "Should be working, as time is set to 10000.");
        Thread.sleep(3000);

        ResponseEntity<JobStatusResponse> queryEntity = restTemplate
            .getForEntity("http://localhost:" + port + "/job/" + initialStatus.getId(), JobStatusResponse.class,
                Collections.emptyMap());

        assertEquals(200, queryEntity.getStatusCodeValue(), "Should return with HTTP OK.");
        assertNotNull(queryEntity.getBody(), "Should have a body returned.");

        JobStatusResponse updatedStatus = queryEntity.getBody();
        assertEquals(JobState.FINISHED, updatedStatus.getStatus(), "Should be finished, as time for work passed.");
    }


    @Test
    @DisplayName("Should be able to submit and cancel job. It should not run to completion.")
    void shouldBeAbleToCancelJob() {
        RestTemplate restTemplate = new RestTemplate();
        CreateJobRequest request = new CreateJobRequest(5000);
        ResponseEntity<JobStatusResponse> jobStatusResponseEntity = restTemplate
            .postForEntity("http://localhost:" + port + "/job", request, JobStatusResponse.class,
                Collections.emptyMap());

        assertEquals(201, jobStatusResponseEntity.getStatusCodeValue(), "Should return with HTTP OK.");
        assertNotNull(jobStatusResponseEntity.getBody(), "Should have a body returned.");

        JobStatusResponse initialStatus = jobStatusResponseEntity.getBody();
        assertEquals(JobState.IN_PROGRESS, initialStatus.getStatus(), "Should be working, as time is set to 10000.");

        ResponseEntity<JobStatusResponse> queryEntity = restTemplate
            .postForEntity("http://localhost:" + port + "/job/" + initialStatus.getId() + "/cancel", null,
                JobStatusResponse.class, Collections.emptyMap());

        assertEquals(200, queryEntity.getStatusCodeValue(), "Should return with HTTP OK.");
        assertNotNull(queryEntity.getBody(), "Should have a body returned.");

        JobStatusResponse updatedStatus = queryEntity.getBody();
        assertEquals(JobState.CANCELLED, updatedStatus.getStatus(),
            "Should be cancelled, as we cancelled before work finished.");
    }


    @Test
    @DisplayName("Should be able to submit and cancel job. It should be finished if we cancel after runtime has completed.")
    void shouldNotBeCancelled() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        CreateJobRequest request = new CreateJobRequest(3000);
        ResponseEntity<JobStatusResponse> jobStatusResponseEntity = restTemplate
            .postForEntity("http://localhost:" + port + "/job", request, JobStatusResponse.class,
                Collections.emptyMap());

        assertEquals(201, jobStatusResponseEntity.getStatusCodeValue(), "Should return with HTTP OK.");
        assertNotNull(jobStatusResponseEntity.getBody(), "Should have a body returned.");

        JobStatusResponse initialStatus = jobStatusResponseEntity.getBody();
        assertEquals(JobState.IN_PROGRESS, initialStatus.getStatus(), "Should be working, as time is set to 10000.");
        Thread.sleep(5000);

        ResponseEntity<JobStatusResponse> queryEntity = restTemplate
            .postForEntity("http://localhost:" + port + "/job/" + initialStatus.getId() + "/cancel", null,
                JobStatusResponse.class, Collections.emptyMap());

        assertEquals(200, queryEntity.getStatusCodeValue(), "Should return with HTTP OK.");
        assertNotNull(queryEntity.getBody(), "Should have a body returned.");

        JobStatusResponse updatedStatus = queryEntity.getBody();
        assertEquals(JobState.FINISHED, updatedStatus.getStatus(),
            "Should be finished, as time for work passed and could no longer cancel.");
    }

    @Test
    @DisplayName("Should be able to query all jobs, completed or not.")
    void queryAll() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        CreateJobRequest j1 = new CreateJobRequest(3000);
        CreateJobRequest j2 = new CreateJobRequest(3000);
        CreateJobRequest j3 = new CreateJobRequest(3000);
        CreateJobRequest j4 = new CreateJobRequest(3000);
        CreateJobRequest j5 = new CreateJobRequest(3000);
        restTemplate
            .postForEntity("http://localhost:" + port + "/job", j1, JobStatusResponse.class, Collections.emptyMap());
        restTemplate
            .postForEntity("http://localhost:" + port + "/job", j2, JobStatusResponse.class, Collections.emptyMap());
        restTemplate
            .postForEntity("http://localhost:" + port + "/job", j3, JobStatusResponse.class, Collections.emptyMap());
        restTemplate
            .postForEntity("http://localhost:" + port + "/job", j4, JobStatusResponse.class, Collections.emptyMap());
        restTemplate
            .postForEntity("http://localhost:" + port + "/job", j5, JobStatusResponse.class, Collections.emptyMap());

        Thread.sleep(5000);

        ResponseEntity<JobStatusResponse[]> getAllJobs = restTemplate
            .getForEntity("http://localhost:" + port + "/job", JobStatusResponse[].class, Collections.emptyMap());

        assertEquals(200, getAllJobs.getStatusCodeValue(), "Should return with HTTP OK.");
        assertNotNull(getAllJobs.getBody(), "Should have a body returned.");

        List<JobStatusResponse> body = Arrays.asList(getAllJobs.getBody());
        body.forEach(jobStatusResponse -> assertEquals(JobState.FINISHED, jobStatusResponse.getStatus(),
            "Should be finished, as time for work passed."));
    }


    @Test
    @DisplayName("Should not be able to query non-existing jobs.")
    void queryNonExistingJob() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new NoOpResponseErrorHandler());
        ResponseEntity<JobStatusResponse> queryEntity = restTemplate
            .getForEntity("http://localhost:" + port + "/job/some-id", JobStatusResponse.class,
                Collections.emptyMap());

        assertEquals(404, queryEntity.getStatusCodeValue(), "Should return with HTTP NOT_FOUND.");
        assertNull(queryEntity.getBody(), "Should not have a body returned.");
    }

    private static class NoOpResponseErrorHandler extends DefaultResponseErrorHandler {

        private NoOpResponseErrorHandler() {
        }

        public void handleError(ClientHttpResponse response) {
        }
    }
}
