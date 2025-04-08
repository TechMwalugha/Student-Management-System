package com.example.backend.services;

import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Iterator;

@Service
public class FileProcessingService {

    private static final String EXCEL_FILE_PATH = "C:/var/log/applications/API/dataprocessing/students.xlsx";
    private static final String CSV_FILE_PATH = "C:/var/log/applications/API/dataprocessing/students.csv";

    public String processExcelToCSV() throws IOException {
        long start = System.nanoTime();

        // Initialize file input and output
        long initStart = System.nanoTime();
        try (
                InputStream is = new FileInputStream(EXCEL_FILE_PATH);
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(is);
                BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH), 16 * 1024); // 16KB buffer
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                        "studentId", "firstName", "lastName", "DOB", "class", "score", "status", "photoPath"
                ))
        ) {
            long initEnd = System.nanoTime();
            System.out.printf("🕒 Initialization Time: %.2f minutes%n", (initEnd - initStart) / 1_000_000_000.0 / 60);

            // Read the sheet and process rows
            long readStart = System.nanoTime();
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            // Skip header row
            if (iterator.hasNext()) iterator.next();

            while (iterator.hasNext()) {
                Row row = iterator.next();
                String[] data = new String[8];

                for (int i = 0; i < 8; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    data[i] = getCellValue(cell);
                }

                try {
                    int studentId = (int) Double.parseDouble(data[0]);
                    String firstName = data[1];
                    String lastName = data[2];
                    String dob = data[3];
                    String studentClass = data[4];
                    double rawScore = Double.parseDouble(data[5]);
                    int score = (int) rawScore + 10;
                    int status = (int) Double.parseDouble(data[6]);
                    String photoPath = data[7];

                    csvPrinter.printRecord(studentId, firstName, lastName, dob, studentClass, score, status, photoPath);
                } catch (Exception e) {
                    System.err.println("Skipped invalid row: " + String.join(",", data) + " - " + e.getMessage());
                }
            }
            long readEnd = System.nanoTime();
            System.out.printf("🕒 Read and Process Time: %.2f minutes%n", (readEnd - readStart) / 1_000_000_000.0 / 60);

            // Flush CSV printer
            long flushStart = System.nanoTime();
            csvPrinter.flush(); // flush once at the end
            long flushEnd = System.nanoTime();
            System.out.printf("🕒 CSV Flush Time: %.2f minutes%n", (flushEnd - flushStart) / 1_000_000_000.0 / 60);
        }

        long end = System.nanoTime();
        System.out.printf("✅ Total CSV Generation Time: %.2f minutes%n", (end - start) / 1_000_000_000.0 / 60);
        return "CSV file successfully created at " + CSV_FILE_PATH;
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell)
                        ? cell.getDateCellValue().toString()
                        : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }
}
