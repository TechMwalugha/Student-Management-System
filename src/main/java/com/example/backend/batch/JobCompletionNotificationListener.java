package com.example.backend.batch;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    @Override
    public void beforeJob (JobExecution jobExecution) {

    }

    @Override
    public void afterJob (JobExecution jobExecution) {
        if(jobExecution.getStatus().isUnsuccessful()) {
            Throwable exception = jobExecution.getAllFailureExceptions().get(0);
            System.err.println("Job failed with exception: " + exception.getMessage());
        } else {
            System.out.println("Job completed successfully");
        }
    }
}
