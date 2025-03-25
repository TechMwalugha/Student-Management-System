package com.example.backend.services;

import com.example.backend.entities.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class StudentReportService {

    @Autowired
    private StudentService studentService;

    public ByteArrayInputStream exportFilteredStudentsToExcel(Long studentId, String studentClass, Date startDate, Date endDate, Pageable pageable) throws IOException {
        Page<Student> students = studentService.searchStudents(studentId, studentClass, startDate, endDate, pageable);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Filtered Students");

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Student ID", "First Name", "Last Name", "DOB", "Class", "Score", "Status", "Photo Path"};

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Write Student Data
            int rowIndex = 1;
            for (Student student : students.getContent()) { // Fix: Use getContent() for Page<Student>
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getFirstName());
                row.createCell(2).setCellValue(student.getLastName());
                row.createCell(3).setCellValue(student.getDob().toString());
                row.createCell(4).setCellValue(student.getStudentClass());
                row.createCell(5).setCellValue(student.getScore());
                row.createCell(6).setCellValue(student.getStatus());
                row.createCell(7).setCellValue(student.getPhotoPath());
            }

            workbook.write(out); // Write before closing
            return new ByteArrayInputStream(out.toByteArray()); // Return the stream
        }
    }
}
