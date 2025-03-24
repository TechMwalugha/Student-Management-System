package com.example.backend.services;

import com.example.backend.entities.Student;
import com.example.backend.repositories.StudentRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class FileUploadService {

    @Autowired
    private StudentRepository  studentRepository;

    private static final String EXCEL_FILE_PATH = "C:/var/log/applications/API/dataprocessing/students.xlsx";

    public String uploadExcelToDatabase() throws IOException {
        File excelFile = new File(EXCEL_FILE_PATH);

        if(!excelFile.exists()) {
            throw new IOException("Excel file not found at:  " + EXCEL_FILE_PATH);
        }

        FileInputStream fis = new FileInputStream(excelFile);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Skip the header row

        List<Student> studentList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            Student student = new Student();
//            student.setStudentId((long) row.getCell(0).getNumericCellValue());
            student.setFirstName(row.getCell(1).getStringCellValue());
            student.setLastName(row.getCell(2).getStringCellValue());

            try {
                student.setDob(dateFormat.parse(row.getCell(3).getStringCellValue()));
            } catch (Exception e) {
                student.setDob(null); // Handle invalid date format
            }

            student.setStudentClass(row.getCell(4).getStringCellValue());
            int score = (int) row.getCell(5).getNumericCellValue() + 5; // Add 5 to score
            student.setScore(score);
            student.setStatus((int) row.getCell(6).getNumericCellValue());
            student.setPhotoPath(row.getCell(7).getStringCellValue());

            studentList.add(student);
        }

        workbook.close();

        // Save students to the database
        studentRepository.saveAll(studentList);

        return "Successfully uploaded " + studentList.size() + " records to the database!";
    }
}
