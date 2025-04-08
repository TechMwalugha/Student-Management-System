package com.example.backend.batch.upload;


import com.example.backend.batch.JobCompletionNotificationListener;
import com.example.backend.batch.generate.StudentDataGeneratorTasklet;
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
public class UploadToDBJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UploadToDBTasklet tasklet;

    public UploadToDBJobConfig(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                               UploadToDBTasklet tasklet) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.tasklet = tasklet;
    }

    @Bean
    public Job UploadToDBJob(JobCompletionNotificationListener listener) {
        return new JobBuilder("UploadToDBJob", jobRepository)
                .listener(listener)
                .start(UploadToDBJobStep())
                .build();
    }

    @Bean
    public Step UploadToDBJobStep() {
        return new StepBuilder("UploadToDBJobStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}
