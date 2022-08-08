package org.dataspread.sheetanalyzer.mainTest;

import org.apache.poi.ss.usermodel.*;
import org.dataspread.sheetanalyzer.SheetAnalyzer;
import org.dataspread.sheetanalyzer.parser.SpreadsheetParser;
import org.dataspread.sheetanalyzer.parser.POIParser;
import org.dataspread.sheetanalyzer.util.SheetData;
import org.dataspread.sheetanalyzer.util.SheetNotSupportedException;

import java.io.*;
import java.util.HashMap;


public class DepRefTest {
    static String filelistColumnName = "File name";

    public static void main(String[] args) throws IOException {

        if (!checkArgs(args)) {
            String warnings = "To run DepRefest, we need 6 arguments: \n" +
                    "1) Path of a xls(x) files containing 'File name' and 'Def Ref' \n" +
                    "2) SheetName \n" +
                    "3) Path of output result \n" +
                    "4) File directory \n" +
                    "5) 'M'/'m' for mostDep and 'L'/'l' for longestDep \n" +
                    "6) TACO or NoComp \n";
            System.out.println(warnings);
            System.exit(-1);
        }

        String inputPath = args[0];
        String sheetName = args[1];
        String outputPath = args[2];
        String fileDir = args[3];

        boolean isMostDep = args[4].equals("M") || args[4].equals("m");
        boolean isCompression = args[5].equals("TACO");
        String modelName = args[5];
        String targetColumn = "Dep Ref";
        if (isMostDep) {
            targetColumn = "Max " + targetColumn;
        } else {
            targetColumn = "Longest " + targetColumn;
        }

        HashMap<String, String> fileNameDepRefMap
                = parseInputFile(inputPath, sheetName, filelistColumnName, targetColumn);

        if (fileNameDepRefMap.size() == 0) {
            System.out.println("Failed to parse " + inputPath);
            System.exit(-1);
        }

        int counter = 0;
        try (PrintWriter statPW = new PrintWriter(new FileWriter(outputPath, false))) {
            // Write header in output file
            String stringBuilder = "fileName" + "," +
                    targetColumn + "," +
                    modelName + "LookupSize" + "," +
                    modelName + "LookUpTime" + "," +
                    modelName + "PostProcessedLookupSize" + "," +
                    modelName + "PostProcessedLookupTime" + "\n";
            statPW.write(stringBuilder);

            for (String fileName: fileNameDepRefMap.keySet()) {
                String depLoc = fileNameDepRefMap.get(fileName);
                counter += 1;
                if (counter >= 5) {
                    break;
                }
                System.out.println("[" + counter + "/" + fileNameDepRefMap.size() + "]: " + "processing " + fileName);
                MainTestUtil.TestRefDependent(statPW, fileDir, fileName, depLoc, isCompression);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, String> parseInputFile(String filePath, String sheetName,
                                                          String filelistColumnName,
                                                          String depRefListColumnName) throws IOException {
        HashMap<String, String> fileNameDepRefMap = new HashMap<>();

        File inputFile = new File(filePath);
        Workbook workbook = WorkbookFactory.create(inputFile);
        Sheet sheet = workbook.getSheet(sheetName);
        int maxRows = 0;
        int maxCols = 0;
        for (Row row : sheet) {
            for (Cell cell : row)
                if (cell.getColumnIndex() > maxCols) maxCols = cell.getColumnIndex();
            if (row.getRowNum() > maxRows) maxRows = row.getRowNum();
        }

        // Find the column index
        int fileColumnIndex = -1, depColumnIndex = -1;
        for (int j = 0; j < maxCols; j++) {
            Row row = sheet.getRow(0);
            if (row != null) {
                Cell cell = row.getCell(j);
                if (cell.getCellType() == CellType.STRING) {
                    if (cell.getStringCellValue().equals(filelistColumnName)) {
                        fileColumnIndex = j;
                    }
                    if (cell.getStringCellValue().equals(depRefListColumnName)) {
                        depColumnIndex = j;
                    }
                }
            }
        }

        if (fileColumnIndex == -1 || depColumnIndex == -1) {
            System.out.println("Cannot find " + filelistColumnName + " and " + depRefListColumnName);
            return fileNameDepRefMap;
        }

        for (int i = 1; i < maxRows; i++) {
            String fileName = null, depLoc = null;
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell fileNameCell = row.getCell(fileColumnIndex);
                Cell depCell = row.getCell(depColumnIndex);
                if (fileNameCell.getCellType() == CellType.STRING) {
                    fileName = fileNameCell.getStringCellValue();
                }
                if (depCell.getCellType() == CellType.STRING) {
                    depLoc = depCell.getStringCellValue();
                }
            }
            if (fileName != null && depLoc != null) {
                fileNameDepRefMap.put(fileName, depLoc);
            }
        }

        return fileNameDepRefMap;
    }

    private static boolean checkArgs(String[] args) {
        if (args.length != 6) {
            return false;
        }

        File inputFile = new File(args[0]);
        File fileDir = new File(args[3]);
        if (!inputFile.exists() || !fileDir.exists()) {
            return false;
        }

        if (!(args[4].equals("m") || args[4].equals("M") || args[4].equals("L") || args[4].equals("l"))) {
            return false;
        }

        if (!(args[5].equals("TACO") || args[5].equals("NoComp"))) {
            return false;
        }

        return true;
    }
}
