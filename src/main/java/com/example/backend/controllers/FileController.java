package com.example.backend.controllers;

import com.example.backend.services.FileProcessingService;
import com.example.backend.services.FileUploadService;
import com.example.backend.services.StudentDataGenerator;
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

    @PostMapping("/generate/{count}")
    public ResponseEntity<Map<String, String>> generateFile(@PathVariable int count) {
        try {
            String filePath = studentDataGenerator.generateStudentData(count);

            Map<String, String> response = new HashMap<>();

            response.put("message", "Students generated successfully");
            response.put("file", filePath);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();

            response.put("message", e.getMessage());
            response.put("file", "none");
            return ResponseEntity.status(500).body(response);
        }

    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> processFile() {
        try {
            String message = fileProcessingService.processExcelToCSV();
            return ResponseEntity.ok(Collections.singletonMap("message", message));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", e.getMessage()));
        }

    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFileToDatabase() {
        try {
            String message = fileUploadService.uploadExcelToDatabase();
            return ResponseEntity.ok(Collections.singletonMap("message", message));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", e.getMessage()));
        }
    }


}
