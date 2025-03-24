package com.example.backend.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class FileProcessingService {
    private static final String EXCEL_FILE_PATH = "C:/var/log/applications/API/dataprocessing/students.xlsx";
    private static final String CSV_FILE_PATH = "C:/var/log/applications/API/dataprocessing/students.csv";

    public String processExcelToCSV() throws IOException {
        File excelFile = new File(EXCEL_FILE_PATH);
        if(!excelFile.exists()) {
            throw new FileNotFoundException("Excel file not found at path: " + EXCEL_FILE_PATH);
        }

        //Read the Excel File
        FileInputStream fis = new FileInputStream(excelFile);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        // Prepare CSV file for writing
        BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                "studentId", "firstName", "lastName", "DOB", "class", "score", "status", "photoPath"
        ));

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); //Skip header row

        List<String[]> dataList = new ArrayList<>();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            int studentId = (int) row.getCell(0).getNumericCellValue();
            String firstName = row.getCell(1).getStringCellValue();
            String lastName = row.getCell(2).getStringCellValue();
            String dob = row.getCell(3).getStringCellValue();
            String studentClass = row.getCell(4).getStringCellValue();
            int score = (int) row.getCell(5).getNumericCellValue() + 10; // Add 10 to score
            int status = (int) row.getCell(6).getNumericCellValue();
            String photoPath = row.getCell(7).getStringCellValue();

            // Add processed data to CSV
            dataList.add(new String[]{String.valueOf(studentId), firstName, lastName, dob, studentClass,
                    String.valueOf(score), String.valueOf(status), photoPath});
        }

        for(String[] data : dataList) {
            csvPrinter.printRecord((Object[]) data);
        }

        workbook.close();
        csvPrinter.close();
        writer.close();

        return "CSV file successfully created at " + CSV_FILE_PATH;
    }
}
