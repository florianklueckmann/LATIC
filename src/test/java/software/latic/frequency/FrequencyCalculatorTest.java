package software.latic.frequency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FrequencyCalculatorTest {

    FrequencyCalculator frequencyCalculator;

    Map<String, Integer> frequencyMap = Map.ofEntries(
            Map.entry("pineapple", 1234),
            Map.entry("apple", 100),
            Map.entry("banana", 50),
            Map.entry("orange", 33)
    );

    List<String> lines = List.of(
            "pineapple;1234",
            "apple;100",
            "banana;50",
            "orange;33"
    );

    Integer highestFrequency = 1234;

    @BeforeEach
    void setUp() {
        frequencyCalculator = new FrequencyCalculator();
        frequencyCalculator.initialize();

        frequencyCalculator.setHighestFrequency(highestFrequency);
        frequencyCalculator.setFrequencyMap(frequencyMap);

    }

    @Test
    void testGetFrequencyMap_returnsCorrectMap() {
        FrequencyCalculator analyzer = new FrequencyCalculator();

        Map<String, Integer> result = analyzer.getFrequencyMap(lines);
        assertEquals(frequencyMap, result);
    }

    @Test
    void testGetFrequencyMap_emptyList_returnsEmptyMap() {
        List<String> lines = List.of();

        Map<String, Integer> result = frequencyCalculator.getFrequencyMap(lines);

        Map<String, Integer> expected = Map.of();
        assertEquals(expected, result);
    }

    @Test
    void testGetFrequencyMap_invalidLine_ignoresInvalidLine() {
        List<String> lines = List.of(
                "apple;10",
                "banana",
                "orange;3"
        );

        Map<String, Integer> result = frequencyCalculator.getFrequencyMap(lines);

        Map<String, Integer> expected = Map.of(
                "apple", 10,
                "orange", 3
        );
        assertEquals(expected, result);
    }

    @Test
    void testCalculateAverageWordFrequency_returnsCorrectAverage() {
        List<String> words = Arrays.asList("pineapple", "apple", "banana", "orange");

        double result = frequencyCalculator.calculateAverageWordFrequency(words, frequencyMap);

        assertEquals(354.25, result, 0.1); // Update the expected value based on your calculation
    }

    @Test
    void testCalculateFrequencyClassNotRounded_realFrequency_returnsCorrectValue() {
        double result = frequencyCalculator.calculateFrequencyClassNotRounded(5, highestFrequency);
        assertEquals(7.94719858426, result, 0.0001);
    }

    @Test
    void testCalculateFrequencyClass_realFrequency_returnsCorrectValue() {
        int result = frequencyCalculator.calculateFrequencyClass(5);
        assertEquals(8, result);
    }

    @Test
    void testCalculateFrequencyClass_higherFrequency_returnsCorrectValue() {
        int result = frequencyCalculator.calculateFrequencyClass(50, 100);
        assertEquals(1, result);
    }

    @Test
    void testCalculateFrequencyClass_equalFrequency_returnsCorrectValue() {
        int result = frequencyCalculator.calculateFrequencyClass(80, 80);
        assertEquals(0, result);
    }

    @Test
    void testCalculateFrequencyClass_lowerFrequency_returnsCorrectValue() {
        int result = frequencyCalculator.calculateFrequencyClass(70, 30);
        assertEquals(-1, result);
    }
}