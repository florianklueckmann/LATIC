package dev.florianklueckmann.latic.services;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvBuilder {
    private static final String DEFAULT_SEPARATOR = "~";

    FileChooser fileChooser = new FileChooser();

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(s -> escapeSpecialCharacters(s))
                .collect(Collectors.joining(DEFAULT_SEPARATOR));
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }


    public File writeToFile(File file, List<String[]> dataLines) throws IOException {
        File csvOutputFile = new File(file.getPath());
        try (PrintWriter pw = new PrintWriter(file)) {
            for (String[] data : dataLines) {
                String x = convertToCSV(data);
                pw.println(x);
            }
            for (String[] strings : dataLines) {
                String s = convertToCSV(strings);
            }
        }
        return csvOutputFile;
    }

}
