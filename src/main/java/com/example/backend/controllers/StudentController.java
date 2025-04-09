package com.example.backend.controllers;

import com.example.backend.entities.Student;
import com.example.backend.services.StudentReportService;
import com.example.backend.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentReportService studentReportService;

    @GetMapping
    public ResponseEntity<Page<Student>> findAll(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String studentClass,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            Pageable pageable) {
        Page<Student> students = studentService.searchStudents(studentId, studentClass, startDate, endDate, pageable);

        if(students.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if empty
        }

        return ResponseEntity.ok(students);
    }

    @GetMapping("/student-count")
    public ResponseEntity<Long> findNoOfStudents() {
        Long studentCount = studentService.countAllStudents();

        return ResponseEntity.ok(studentCount);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportFilteredStudentsToExcel(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String studentClass,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            Pageable pageable) {
        try {
            ByteArrayInputStream in = studentReportService.exportFilteredStudentsToExcel(studentId, studentClass, startDate, endDate, pageable);

            byte[] excelBytes = in.readAllBytes();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=filtered_students.xlsx")
                    .body(excelBytes);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{studentID}")
    public ResponseEntity<Map<String, String>> softDeleteStudent(@PathVariable Long studentID) {
        String response = studentService.softDeleteStudent(studentID);

        return ResponseEntity.ok(Collections.singletonMap("message", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateStudent (
            @PathVariable Long id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("dob") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dob,
            @RequestParam("studentClass") String studentClass,
            @RequestParam("score") int score,
            @RequestParam("status") int status,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            // Manually create Student object
            Student updatedStudent = new Student();
            updatedStudent.setFirstName(firstName);
            updatedStudent.setLastName(lastName);
            updatedStudent.setDob(dob);
            updatedStudent.setStudentClass(studentClass);
            updatedStudent.setScore(score);
            updatedStudent.setStatus(status);

            String response = studentService.updateStudent(id, updatedStudent, photo);
            return ResponseEntity.ok(Collections.singletonMap("message", response));

        } catch(IOException e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

}
