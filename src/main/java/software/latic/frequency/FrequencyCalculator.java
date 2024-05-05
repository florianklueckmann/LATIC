package software.latic.frequency;

import software.latic.helper.CsvReader;
import software.latic.translation.SupportedLocales;
import software.latic.translation.Translation;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class FrequencyCalculator {
    private static final FrequencyCalculator FREQUENCY_CALCULATOR = new FrequencyCalculator();
    public static FrequencyCalculator getInstance() {
        return FREQUENCY_CALCULATOR;
    }

    private Map<String, Integer> frequencyMap;
    private int highestFrequency;
    private static final List<Locale> SUPPORTED_LOCALES = List.of(SupportedLocales.GERMAN.getLocale());

    private Locale currentLocale = Translation.getInstance().getLocale();

    public double calculateAverageWordFrequency(List<String> words) {

        int sumFrequency = 0;
        int totalWords = words.size();

        for (String word : words) {
            sumFrequency += frequencyMap.getOrDefault(word, 1);
        }

        return (double) sumFrequency / totalWords;
    }

    private boolean stopInitialize() {
        if (this.currentLocale == null) {
            return false;
        } else {
            return !SUPPORTED_LOCALES.contains(Translation.getInstance().getLocale()) && this.currentLocale == Translation.getInstance().getLocale();
        }
    }
    public void initialize() {
        if (stopInitialize()) {
            return;
        }
        var lines =  CsvReader.getInstance().readFile(String.format("frequency/%s/frequencyList.csv", Translation.getInstance().getLanguageTag()));

        highestFrequency = Integer.parseInt(lines.getFirst().split(";")[1]);

        frequencyMap = getFrequencyMap(lines);
    }

    protected Map<String, Integer> getFrequencyMap(List<String> lines) {
        return lines.stream()
                .filter(line -> line.contains(";"))
                .map(line -> line.split(";"))
                .map(split -> new FrequencyListEntry(split[0], Integer.parseInt(split[1]))).collect(Collectors.toMap(
                        FrequencyListEntry::word,FrequencyListEntry::frequency
                ));
    }

    public double calculateAverageWordFrequency(List<String> words, Map<String, Integer> frequencyMap) {

        int sumFrequency = 0;
        int totalWords = words.size();

        for (String word : words) {
            sumFrequency += frequencyMap.getOrDefault(word, 1);
        }

        return (double) sumFrequency / totalWords;
    }

    protected Integer calculateFrequencyClass(int currentFrequency, int highestFrequency) {
        return (int) Math.round(Math.log((double) highestFrequency / currentFrequency) / Math.log(2));
    }

    protected Integer calculateFrequencyClass(int currentFrequency) {
        return (int) Math.round(Math.log((double) highestFrequency / currentFrequency) / Math.log(2));
    }

    protected double calculateFrequencyClassNotRounded(int currentFrequency, int highestFrequency) {
        return Math.log((double) highestFrequency / currentFrequency) / Math.log(2);
    }

    protected int getHighestFrequency() {
        return highestFrequency;
    }

    protected void setHighestFrequency(int highestFrequency) {
        this.highestFrequency = highestFrequency;
    }

    protected Map<String, Integer> getFrequencyMap() {
        return frequencyMap;
    }

    protected void setFrequencyMap(Map<String, Integer> frequencyMap) {
        this.frequencyMap = frequencyMap;
    }
}
