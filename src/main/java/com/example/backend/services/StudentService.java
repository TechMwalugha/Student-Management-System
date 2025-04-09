package com.example.backend.services;

import com.example.backend.entities.Student;
import com.example.backend.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private static final String PHOTO_DIRECTORY = "C:/var/log/applications/API/StudentPhotos/";

    @Autowired
    private StudentRepository studentRepository;

    public Page<Student> searchStudents(Long studentId, String studentClass, Date startDate, Date endDate, Pageable pageable) {
        if (studentId != null) {
            return studentRepository.findByStudentId(studentId, pageable);
        }

        // Combine conditions for class and date of birth
        if (studentClass != null && !studentClass.isEmpty() && startDate != null && endDate != null) {
            return studentRepository.findByStudentClassAndDobBetween(studentClass, startDate, endDate, pageable);
        }

        // Filter by class only
        if (studentClass != null && !studentClass.isEmpty()) {
            return studentRepository.findByStudentClass(studentClass, pageable);
        }

        // Filter by date of birth range only
        if (startDate != null && endDate != null) {
            return studentRepository.findByDobBetween(startDate, endDate, pageable);
        }

        return studentRepository.findAll(pageable);
    }


    public List<Student> findAllStudents () {
     return studentRepository.findAll();

    }

    // Method to fetch the count of all students
    public long countAllStudents() {
        return studentRepository.count(); // This directly counts the records
    }

    public String softDeleteStudent(Long studentID) {
        Optional studentOptional = studentRepository.findById(studentID);

        if(studentOptional.isPresent()) {
            Student student = (Student) studentOptional.get();

            student.setStatus(0); // Set status to 0 inactive
            studentRepository.save(student);

            return "Student with student Name: " + student.getFirstName() + " soft deleted";
        }

        return "Student with student ID: " + studentID + " not found";
    }

    public String updateStudent(Long studentId, Student updatedStudent, MultipartFile photo) throws IOException {
        Optional<Student> studentOptional = studentRepository.findById(studentId);

        if(!studentOptional.isPresent()) {
            return "Error: Student with ID " + studentId + " not found";
        }

        Student student = studentOptional.get();
        student.setFirstName(updatedStudent.getFirstName());
        student.setLastName(updatedStudent.getLastName());
        student.setDob(updatedStudent.getDob());
        student.setStudentClass(updatedStudent.getStudentClass());
        student.setScore(updatedStudent.getScore());
        student.setStatus(updatedStudent.getStatus());

        // Handle file upload
        if(photo != null && !photo.isEmpty()){
            // Validate file type (PNG & JPEG only)
            String contentType = photo.getContentType();
            if(!("image/png".equals(contentType) || "image/jpeg".equals(contentType))) {
                return "Error: Only PNG and JPEG images are allowed. ";
            }

            // Validate file size (Max 5MB)
            if(photo.getSize() > 5 * 1024 * 1024) {
                return "Error: File size must not exceed 5MB";
            }

            // Create directory if it does not exist
            File directory = new File(PHOTO_DIRECTORY);
            if(!directory.exists()){
                directory.mkdirs();
            }

            // Rename file with student ID prefix
            String fileExtension = contentType.equals("image/png") ? ".png" : ".jpg";
            String newFileName = studentId + "-" + photo.getOriginalFilename();
            File file = new File(PHOTO_DIRECTORY + newFileName);

            // Save file
            Files.copy(photo.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            student.setPhotoPath(file.getAbsolutePath()); // Update photo path in database
        }

        // Save updated student record
        studentRepository.save(student);
        return "Student with ID " + studentId + " updated successfully.";
    }

}
