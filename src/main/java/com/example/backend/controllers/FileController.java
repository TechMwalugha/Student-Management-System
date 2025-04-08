package com.example.backend.controllers;

import com.example.backend.services.FileProcessingService;
import com.example.backend.services.FileUploadService;
import com.example.backend.services.StudentDataGenerator;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private StudentDataGenerator studentDataGenerator;

    @Autowired
    private FileProcessingService fileProcessingService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job generateStudentsJob;

    @Autowired
    private Job SaveExcelFileAsCSVJob;

    @Autowired
    private Job UploadToDBJob;

    @PostMapping("/generate/{count}")
    public ResponseEntity<Map<String, String>> generateFile(@PathVariable int count) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("count", String.valueOf(count))
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(generateStudentsJob, jobParameters);

            if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
                return ResponseEntity.ok(Collections.singletonMap("message", count + " students generated successfully"));
            } else {
                return ResponseEntity.status(500).body(Collections.singletonMap("message", "Job failed with exception: " + jobExecution.getStatus()));
            }

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();

            response.put("message", e.getMessage());
            response.put("file", "none");
            return ResponseEntity.status(500).body(response);
        }

    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> processFile() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(SaveExcelFileAsCSVJob, jobParameters);

            if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
                return ResponseEntity.ok(Collections.singletonMap("message", "Excel file processed and saved as CSV file successfully"));
            } else {
                return ResponseEntity.status(500).body(Collections.singletonMap("message", "Job failed with status: " + jobExecution.getStatus()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", e.getMessage()));
        }

    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFileToDatabase() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

           JobExecution jobExecution = jobLauncher.run(UploadToDBJob, jobParameters);

           if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
               return ResponseEntity.ok(Collections.singletonMap("message", "Students uploaded to DB successfully"));
           } else {
               return ResponseEntity.status(500).body(Collections.singletonMap("message", "Job failed with status: " + jobExecution.getStatus()));
           }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", e.getMessage()));
        }
    }


}
