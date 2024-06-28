package software.latic.frequency;

import software.latic.helper.CsvReader;
import software.latic.translation.SupportedLocales;
import software.latic.translation.Translation;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FrequencyCalculator {
    private static final FrequencyCalculator FREQUENCY_CALCULATOR = new FrequencyCalculator();
    public static FrequencyCalculator getInstance() {
        return FREQUENCY_CALCULATOR;
    }

    private Map<String, Integer> frequencyMap;
    private int highestFrequency;
    private static final List<Locale> SUPPORTED_LOCALES = List.of(SupportedLocales.GERMAN.getLocale());

    private Locale currentLocale;

    public double calculateAverageWordFrequencyClass(List<String> words) {

        int sumFrequencyClass = 0;
        int totalWords = words.size();

        for (String word : words) {
            var wordFrequencyClass = calculateFrequencyClass(frequencyMap.getOrDefault(word.toLowerCase(Translation.getInstance().getLocale()), 1));
            Logger.getLogger("FrequencyCalculator").log(Level.INFO, String.format("FrequencyClass %s %s", word, wordFrequencyClass));

            sumFrequencyClass += wordFrequencyClass;
        }

        return (double) sumFrequencyClass / totalWords;
    }

    public double calculateAverageWordFrequency(List<String> words) {
        int sumFrequency = 0;
        int totalWords = words.size();

        for (String word : words) {
            sumFrequency += frequencyMap.getOrDefault(word.toLowerCase(Translation.getInstance().getLocale()), 1);
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
        currentLocale = Translation.getInstance().getLocale();
        if (stopInitialize()) {
            return;
        }
        var lines =  CsvReader.getInstance().readFile(String.format("frequency/%s/frequencies.csv", Translation.getInstance().getLanguageTag()));

        var list = new ArrayList<>(lines.stream().map(line -> new FrequencyListEntry(line.split(";")[0], Integer.parseInt(line.split(";")[1]))).toList());

        list.sort(Comparator.comparingInt(FrequencyListEntry::frequency).reversed());
        highestFrequency = list.getFirst().frequency();

        Logger.getLogger("FrequencyCalculator").log(Level.INFO, String.format("Highest frequency %s %s", list.getFirst().word(), list.getFirst().frequency()) );

        frequencyMap = list.stream().map(entry -> Map.entry(entry.word(), entry.frequency())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
    }

    protected ConcurrentMap<String, Integer> getFrequencyMap(List<String> lines) {
        return lines.stream()
                .filter(line -> line.contains(";"))
                .map(line -> line.split(";"))
                .map(split -> new FrequencyListEntry(split[0], Integer.parseInt(split[1]))).collect(Collectors.toConcurrentMap(
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

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    protected Integer calculateFrequencyClass(int currentFrequency, int highestFrequency) {
        return (int) Math.round(log2((double) highestFrequency / currentFrequency));
    }

    protected Integer calculateFrequencyClass(int currentFrequency) {
        Logger.getLogger("FrequencyCalculator").log(Level.INFO, String.format("current: %s highest: %s", currentFrequency, getHighestFrequency()));
        return this.calculateFrequencyClass(currentFrequency, getHighestFrequency());
    }

    protected double calculateFrequencyClassNotRounded(int currentFrequency, int highestFrequency) {
        return log2((double) highestFrequency / currentFrequency);
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
