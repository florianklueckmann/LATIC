package software.latic.helper;

import software.latic.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class CsvReader {
    private static final CsvReader csvReader = new CsvReader();
    public static CsvReader getInstance() {
        return csvReader;
    }


    public List<String> readFile(String fileName) {
        List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(Objects.requireNonNull(
                App.class.getResourceAsStream(fileName), "fileName must not be null"))) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        }

        return lines;
    }
}
