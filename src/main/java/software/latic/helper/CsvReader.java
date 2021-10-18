package software.latic.helper;

import org.apache.log4j.Logger;
import software.latic.App;

import java.util.*;
import java.util.stream.Collectors;

public class CsvReader {
    private static final CsvReader csvReader = new CsvReader();
    public static CsvReader getInstance() {
        return csvReader;
    }


    public List<String> readFile(String fileName) {
        List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(Objects.requireNonNull(
                App.class.getResourceAsStream(fileName), String.format("readFile(%s): Unable to find file.", fileName)))) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } catch (NullPointerException e) {
            Logger.getLogger("CsvReader").warn(e.getMessage());
        }

        return lines;
    }

    public Map<String, String> convertCsvToMap(String fileName, String separator) {
        return readFile(fileName).stream()
                .map(dataLine -> dataLine.split(separator))
                .collect(Collectors.toMap(word -> word[0], replacement -> replacement[1]));
    }
}
