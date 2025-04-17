package com.example.backend.services;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
    private static final List<String> SAMPLE_NAMES = List.of(
            "Ensley", "Wolf", "Jase", "Tucker", "Esther", "Stevens", "Zachary", "Duke", "Melani", "English",
            "Junior", "Rosas", "Joelle", "Schwartz", "Edwin", "Erickson", "Sabrina", "Soto", "Barrett", "Macdonald",
            "Rosalia", "Castillo", "Kai", "Bishop", "Brooklynn", "Blankenship", "Ernesto", "Rivers", "Kiana",
            "Merritt", "Colten", "Barnett", "Harlow", "Stone", "Finn", "Branch", "Luisa", "McClain", "Mitchell",
            "Stout", "Chana", "McKenzie", "Scott", "Diaz", "Elena", "Medina", "George", "Malone", "Skyler",
            "Woodward", "Jeremias", "Richmond", "Whitney", "Dyer", "Atreus", "Robertson", "Harmony", "Andersen",
            "Alistair", "Copeland", "Dayana", "Caldwell", "Rylan", "Weiss", "Lennox", "Gross", "Quinn", "Rowe",
            "Matilda", "Porter", "Rhett", "Kaur", "Holland", "Walls", "Larry", "Bruce", "Marilyn", "Chen",
            "Emmanuel", "Simon", "Kalani", "Dodson", "Seven", "Dominguez", "Raegan", "Doyle", "Annalise",
            "Stephenson", "Joe", "Hanson", "Mariana", "Burns", "August", "Estrella", "Lugo"
    );
    private static final Random random = new Random();

    public String generateStudentData(int count) throws IOException {
        long totalStart = System.nanoTime();

        String filePath = "C:/var/log/applications/API/dataprocessing/students.xlsx";
        File directory = new File("C:/var/log/applications/API/dataprocessing/");
        if (!directory.exists()) directory.mkdirs();

        long initStart = System.nanoTime();
        try (
                SXSSFWorkbook workbook = new SXSSFWorkbook(20000);  // keeps 20k rows in memory before flushing
                FileOutputStream fileOut = new FileOutputStream(filePath)
        ) {
            long initEnd = System.nanoTime();
            System.out.printf("🕒 Initialization Time: %.2f minutes%n", (initEnd - initStart) / 1_000_000_000.0 / 60);

            long headerStart = System.nanoTime();
            SXSSFSheet sheet = workbook.createSheet("Students");

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"studentId", "firstName", "lastName", "DOB", "class", "score", "status", "photoPath"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            long headerEnd = System.nanoTime();
            System.out.printf("🕒 Header Creation Time: %.2f minutes%n", (headerEnd - headerStart) / 1_000_000_000.0 / 60);

            long dataStart = System.nanoTime();
            // Data rows
            for (int i = 1; i <= count; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue(getRandomSampleName());
                row.createCell(2).setCellValue(getRandomSampleName());
                row.createCell(3).setCellValue(generateRandomDOB());
                row.createCell(4).setCellValue(CLASS_OPTIONS.get(random.nextInt(CLASS_OPTIONS.size())));
                row.createCell(5).setCellValue(random.nextInt(31) + 55); // score between 55-85
                row.createCell(6).setCellValue(1); // status
                row.createCell(7).setCellValue(""); // photoPath

                if (i % 20000 == 0) {
                    sheet.flushRows(); // Flush memory to disk every 20k rows
                }
            }
            long dataEnd = System.nanoTime();
            System.out.printf("🕒 Data Generation Time: %.2f minutes%n", (dataEnd - dataStart) / 1_000_000_000.0 / 60);

            long saveStart = System.nanoTime();
            workbook.write(fileOut); // Save the file
            long saveEnd = System.nanoTime();
            System.out.printf("🕒 File Save Time: %.2f minutes%n", (saveEnd - saveStart) / 1_000_000_000.0 / 60);
        }

        long totalEnd = System.nanoTime();
        System.out.printf("✅ Total Execution Time: %.2f minutes%n", (totalEnd - totalStart) / 1_000_000_000.0 / 60);
        return filePath;
    }

    private String getRandomSampleName() {
        return SAMPLE_NAMES.get(random.nextInt(SAMPLE_NAMES.size()));
    }

    private String generateRandomDOB() {
        return String.format("%04d-%02d-%02d",
                2000 + random.nextInt(11), // 2000-2010
                1 + random.nextInt(12),    // Month
                1 + random.nextInt(28));   // Day (safe for all months)
    }
}