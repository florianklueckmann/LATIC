package software.latic.frequency;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import software.latic.Logging;
import software.latic.helper.CsvReader;
import software.latic.helper.FileContentProvider;
import software.latic.translation.Translation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FrequencyListCleaner {
    private String replaceNonUTF8(String input) {
//        Charset utf8Charset = StandardCharsets.UTF_8;
        Charset utf8Charset = StandardCharsets.ISO_8859_1;
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (utf8Charset.newEncoder().canEncode(c)) {
                result.append(c);
            }
            // You can add additional logic here for handling specific non-UTF-8 characters if needed.
        }

        return result.toString();
    }

    private List<FrequencyListEntry> removeDuplicates(List<FrequencyListEntry> list) {
        var freqMap = new HashMap<String, FrequencyListEntry>();

        for (var entry : list) {

            if (freqMap.containsKey(entry.word())) {
                freqMap.get(entry.word()).setFrequency(freqMap.get(entry.word()).frequency() + entry.frequency());
            } else {
                freqMap.put(entry.word(), entry);
            }
        }

        return freqMap.values().stream().toList();
    }

    public List<String[]> cleanCleanedCsv(File importedFile) {
        ObservableList<CharSequence> content = null;

        var specialCharMap = CsvReader.getInstance().convertCsvToMap("frequency/specialCharsHtmlCodes.csv", ",");

        try {
            content = FXCollections
                    .observableList(FileContentProvider
                            .getContent(importedFile.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<FrequencyListEntry> frequencyList = new ArrayList<>();

        var pattern = Pattern.compile("^\\d+\\t\\p{L}+\\t\\d+$", Pattern.CASE_INSENSITIVE);

        for (var row : content) {

            row = replaceSpecialHtmlCharacterCodesWithUTF8Characters(
                    replaceNonUTF8(
                            row.toString().toLowerCase(Translation.getInstance().getLocale())
                    ),
                    specialCharMap
            );

//            if (!pattern.matcher(row).matches()) {
//                Logging.getInstance().debug("FrequencyListCleaner", "Line does not match: " + row.toString());
//                continue;
//            }

            var data = row.toString().split(";");

            var word = data[0];

            var listEntry = new FrequencyListEntry(word, Integer.parseInt(data[1]));

            if (shouldAddEntryToFrequencyList(listEntry)) {
                frequencyList.add(listEntry);
            }
        }

        if (!frequencyList.isEmpty()) {
            System.out.println("CSV: " + frequencyList.size());
        }

        frequencyList = removeDuplicates(frequencyList);

        return frequencyList.stream()
                .sorted(Comparator.comparing(FrequencyListEntry::word))
                .map(d -> (d.word() + "," + d.frequency()).split(","))
                .collect(Collectors.toList());
    }

    public List<String[]> cleanCsv(File importedFile) {
        ObservableList<CharSequence> content = null;

        var specialCharMap = CsvReader.getInstance().convertCsvToMap("frequency/specialCharsHtmlCodes.csv", ",");

        try {
            content = FXCollections
                    .observableList(FileContentProvider
                            .getContent(importedFile.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<FrequencyListEntry> frequencyList = new ArrayList<>();

        var pattern = Pattern.compile("^\\d+\\t\\p{L}+\\t\\d+$", Pattern.CASE_INSENSITIVE);

        for (var row : content) {

            row = replaceSpecialHtmlCharacterCodesWithUTF8Characters(
                    replaceNonUTF8(
                            row.toString().toLowerCase(Translation.getInstance().getLocale())
                    ),
                    specialCharMap
            );

            if (!pattern.matcher(row).matches()) {
                Logging.getInstance().debug("FrequencyListCleaner", "Line does not match: " + row.toString());
                continue;
            }

            var data = row.toString().split("\\t");

            var word = data[1];

            var listEntry = new FrequencyListEntry(word, Integer.parseInt(data[2]));

            if (shouldAddEntryToFrequencyList(listEntry)) {
                frequencyList.add(listEntry);
            }
        }

        if (!frequencyList.isEmpty()) {
            System.out.println("CSV: " + frequencyList.size());
        }

        frequencyList = removeDuplicates(frequencyList);

        return frequencyList.stream()
                .sorted(Comparator.comparing(FrequencyListEntry::word))
                .map(d -> (d.word() + "," + d.frequency()).split(","))
                .collect(Collectors.toList());
    }


    private String replaceSpecialHtmlCharacterCodesWithUTF8Characters(String input, Map<String, String> specialCharMap) {
        for (var regex : specialCharMap.keySet()) {
            input = input.replace(regex, specialCharMap.get(regex));
        }
        return input;
    }

    private Boolean shouldAddEntryToFrequencyList(FrequencyListEntry listEntry) {
        return listEntry.word().length() > 1
//                && listEntry.frequency() > 1
                && !listEntry.word().matches("\\d+")
                && !listEntry.word().contains(" ")
                && !listEntry.word().contains("-");
    }
}
