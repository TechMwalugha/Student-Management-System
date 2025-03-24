package com.example.backend.controllers;

import com.example.backend.services.FileProcessingService;
import com.example.backend.services.FileUploadService;
import com.example.backend.services.StudentDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private StudentDataGenerator studentDataGenerator;

    @Autowired
    private FileProcessingService fileProcessingService;

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/generate/{count}")
    public ResponseEntity<String> generateFile(@PathVariable int count) {
        try {
            String filePath = studentDataGenerator.generateStudentData(count);
            return ResponseEntity.ok("File generated successfully " + filePath);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error generating file: " + e.getMessage());
        }
    }

    @PostMapping("/process")
    public ResponseEntity<String> processFile() {
        try {
            String message = fileProcessingService.processExcelToCSV();
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileToDatabase() {
        try {
            String message = fileUploadService.uploadExcelToDatabase();
            return ResponseEntity.ok(message);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file " + e.getMessage());
        }
    }


}
