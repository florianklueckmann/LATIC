package software.latic.helper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvBuilder {
    private static final String DEFAULT_SEPARATOR = ",";

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
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

    public File writeCsvForExcel(File file, List<String[]> dataLines) throws IOException {
        File csvOutputFile = new File(file.getPath());
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println(String.format("sep=%s", DEFAULT_SEPARATOR));
            for (String[] data : dataLines) {
                String x = convertToCSV(data);
                pw.println(x);
            }
        }
        return csvOutputFile;
    }

    public File writeToFile(File file, List<String[]> dataLines) throws IOException {
        File csvOutputFile = new File(file.getPath());
        try (PrintWriter pw = new PrintWriter(file)) {
            for (String[] data : dataLines) {
                String x = convertToCSV(data);
                pw.println(x);
            }
        }
        return csvOutputFile;
    }

}
