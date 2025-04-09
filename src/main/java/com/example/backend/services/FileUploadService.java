package com.example.backend.services;
import com.example.backend.entities.Student;
import com.example.backend.repositories.StudentRepository;
import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FileUploadService {

    @Autowired
    private StudentRepository studentRepository;

    private static final String EXCEL_FILE_PATH = "C:/var/log/applications/API/dataprocessing/students.xlsx";
    private static final int BATCH_SIZE = 1000;

    public String uploadExcelToDatabase() throws Exception {
        List<Student> batch = new ArrayList<>();
        int totalInserted = 0;

        try (FileInputStream fis = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100)      // how many rows to keep in memory
                     .bufferSize(4096)       // how many bytes to read at a time
                     .open(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            boolean isHeader = true;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            while (iterator.hasNext()) {
                Row row = iterator.next();

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                try {
                    Student student = new Student();
                    student.setFirstName(row.getCell(1).getStringCellValue());
                    student.setLastName(row.getCell(2).getStringCellValue());

                    try {
                        student.setDob(dateFormat.parse(row.getCell(3).getStringCellValue()));
                    } catch (Exception e) {
                        student.setDob(null);
                    }

                    student.setStudentClass(row.getCell(4).getStringCellValue());
                    int score = (int) row.getCell(5).getNumericCellValue() + 5;
                    student.setScore(score);
                    student.setStatus((int) row.getCell(6).getNumericCellValue());
                    student.setPhotoPath(row.getCell(7).getStringCellValue());

                    batch.add(student);

                    if (batch.size() >= BATCH_SIZE) {

                        studentRepository.saveAll(batch);
                        totalInserted += batch.size();
                        batch.clear();
                    }
                } catch (Exception e) {
                    System.err.println("Skipped row due to error: " + e.getMessage());
                }
            }

            if (!batch.isEmpty()) {
                studentRepository.saveAll(batch);
                totalInserted += batch.size();
            }
        }

        return "Successfully uploaded " + totalInserted + " students!";
    }
}
