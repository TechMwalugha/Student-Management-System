package com.example.backend.controllers;

import com.example.backend.entities.Student;
import com.example.backend.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/students")
    public ResponseEntity<List<Student>> findAll() {
        List<Student> students = studentService.getAllStudents();

        if(students.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if empty
        }

        return ResponseEntity.ok(students);
    }
}
