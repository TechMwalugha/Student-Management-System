package com.example.backend.batch.generate;

import com.example.backend.batch.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class GenerateStudentsJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StudentDataGeneratorTasklet tasklet;

    public GenerateStudentsJobConfig(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     StudentDataGeneratorTasklet tasklet) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.tasklet = tasklet;
    }

    @Bean
    public Job generateStudentsJob(JobCompletionNotificationListener listener) {
        return new JobBuilder("generateStudentsJob", jobRepository)
                .listener(listener)
                .start(generateStudentsStep())
                .build();
    }

    @Bean
    public Step generateStudentsStep() {
        return new StepBuilder("generateStudentsStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}
