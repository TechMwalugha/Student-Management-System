package com.example.backend.batch.process;

import com.example.backend.batch.JobCompletionNotificationListener;
import com.example.backend.batch.process.SaveExcelFileAsCSVTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.JobBuilderHelper;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.StepBuilderHelper;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class SaveExcelFileAsCSVJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SaveExcelFileAsCSVTasklet tasklet;

    public SaveExcelFileAsCSVJobConfig(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                       SaveExcelFileAsCSVTasklet tasklet) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.tasklet = tasklet;
    }

    @Bean
    public Job SaveExcelFileAsCSVJob(JobCompletionNotificationListener listener) {
        return new JobBuilder("SaveExcelFileAsCSVJob", jobRepository)
                .listener(listener)
                .start(SaveExcelFileAsCSVStep())
                .build();
    }

    @Bean
    public Step SaveExcelFileAsCSVStep() {
        return new StepBuilder("SaveExcelFileAsCSVStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}

