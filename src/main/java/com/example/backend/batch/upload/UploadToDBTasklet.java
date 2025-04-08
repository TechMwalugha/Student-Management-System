package com.example.backend.batch.upload;


import com.example.backend.services.FileUploadService;
import com.example.backend.services.StudentDataGenerator;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UploadToDBTasklet implements Tasklet {

    @Autowired
    private FileUploadService uploadService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        JobParameters parameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
        uploadService.uploadExcelToDatabase();
        return RepeatStatus.FINISHED;
    }
}
