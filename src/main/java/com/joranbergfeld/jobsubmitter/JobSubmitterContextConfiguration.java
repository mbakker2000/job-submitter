package com.joranbergfeld.jobsubmitter;


import com.joranbergfeld.jobsubmitter.domain.JobServiceImpl;
import com.joranbergfeld.jobsubmitter.domain.creator.JobCreator;
import com.joranbergfeld.jobsubmitter.domain.creator.JobCreatorImpl;
import com.joranbergfeld.jobsubmitter.storage.InMemoryJobStorage;
import com.joranbergfeld.jobsubmitter.storage.JobStorage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobSubmitterContextConfiguration {

    @Bean
    ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    JobStorage jobStorage() {
        return new InMemoryJobStorage(LoggerFactory.getLogger(InMemoryJobStorage.class));
    }

    @Bean
    JobCreator jobCreator() {
        return new JobCreatorImpl();
    }

    @Bean
    JobServiceImpl submitterService(ExecutorService executorService, JobStorage jobStorage) {
        return new JobServiceImpl(LoggerFactory.getLogger(JobServiceImpl.class), executorService,
            jobStorage);
    }
}
