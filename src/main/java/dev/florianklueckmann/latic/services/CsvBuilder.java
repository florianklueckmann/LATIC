package dev.florianklueckmann.latic.services;

import javafx.stage.FileChooser;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvBuilder {
    private static final String DEFAULT_SEPARATOR = ",";

    FileChooser fileChooser = new FileChooser();

//    List<String[]> dataLines = new ArrayList();

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(s -> escapeSpecialCharacters(s))
                .collect(Collectors.joining(DEFAULT_SEPARATOR));
    }

    private String escapeSpecialCharacters(String data) {
        System.out.println("ESC: " + data);
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }


//    public void givenDataArray_whenConvertToCSV_thenOutputCreated(File file, List<String[]> dataLines) throws IOException {
    public File writeToFile(File file, List<String[]> dataLines) throws IOException {
        System.out.println(dataLines.get(0)[0]);
        File csvOutputFile = new File(file.getPath());
        try (PrintWriter pw = new PrintWriter(file)) {
            for (String[] data : dataLines) {
                String x = convertToCSV(data);
                pw.println(x);
            }
            for (String[] strings : dataLines) {
                String s = convertToCSV(strings);
                System.out.println(s);
            }
        }
        return csvOutputFile;
//        assertTrue(csvOutputFile.exists());
    }

}
