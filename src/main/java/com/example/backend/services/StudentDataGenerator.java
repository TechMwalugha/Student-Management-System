package com.example.backend.services;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class StudentDataGenerator {
    private static final List<String> CLASS_OPTIONS = Arrays.asList("Class 1", "Class 2", "Class 3", "Class 4", "Class 5");
    private static final Random random = new Random();

    public String generateStudentData (int count) throws IOException {

        //Define file path
        String filePath = "C:/var/log/applications/API/dataprocessing/students.xlsx";
        File directory = new File("C:/var/log/applications/API/dataprocessing/");

        // Ensure directory exists
        if(!directory.exists()) {
            directory.mkdirs();
        }

        // Create a new Excel workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Create Header Row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"studentId", "firstName", "lastName", "DOB", "class", "score", "status", "photoPath"};
        for(int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Generate Student Data
        for(int i = 1; i <= count; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue(generateRandomString(3, 8));
            row.createCell(2).setCellValue(generateRandomString(3, 8));
            row.createCell(3).setCellValue(generateRandomDOB());
            row.createCell(4).setCellValue(CLASS_OPTIONS.get(random.nextInt(CLASS_OPTIONS.size())));
            row.createCell(5).setCellValue(random.nextInt(31) + 55); // Score between 55-85
            row.createCell(6).setCellValue(1); // Default status = 1 (active)
            row.createCell(7).setCellValue(""); // Empty photoPath
        }

        //Save to file
        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

        return filePath;
    }

    private String generateRandomString(int minLength, int maxLength) {
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }

        return sb.toString();
    }

    private String generateRandomDOB() {
        int year = 2000 + random.nextInt(11);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);

        return String.format("%04d-%02d-%02d", year, month, day);
    }

}
