package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.TestItem;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

class SimpleTextAnalyzerTest {

    //TODO Mock the TextFormatting?
    //TextFormattingService mockedTextFormattingService = mock(TextFormattingService.class);

    private final TestItem deItemEarthAndSun = new TestItem(
            "Die Erde dreht sich um die Sonne. Wie lange dauert diese Umlaufzeit ca.?\n" +
                    "12 Stunden.\n" +
                    "1 Tag.\n" +
                    "3 Monate.\n" +
                    "1 Jahr.",
            21, 6, 3.5, 3.90, 13.66);

    private final List<CharSequence> paragraphs = new ArrayList<>(
            Arrays.asList(
                    "Die Erde dreht sich um die Sonne. Wie lange dauert diese Umlaufzeit ca.?",
                    "12 Stunden.",
                    "1 Tag.",
                    "3 Monate.",
                    "1 Jahr."));

    SimpleTextAnalyzer simpleTextAnalyzer;

    @BeforeEach
    void setUp() {
        Properties props = new Properties();

        try {
            props.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
        } catch (IOException e) {
            //TODO: Error Handling
            e.printStackTrace();
        }

        Document doc = new Document(props, deItemEarthAndSun.getText());
        simpleTextAnalyzer = new SimpleTextAnalyzer(new TextFormattingService());
        simpleTextAnalyzer.setDoc(doc);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getWordCount() {
        Assertions.assertEquals(deItemEarthAndSun.getWordCount(), simpleTextAnalyzer.getWordCount());
    }

    @Test
    void getTextCharCountWithoutPuncts() {
        Assertions.assertEquals(82, simpleTextAnalyzer.getTextCharCountWithoutPuncts());
    }

    @Test
    void getAverageWordLengthCharacters() {
        Assertions.assertEquals(deItemEarthAndSun.getAvgWordLengthChar(), simpleTextAnalyzer.getAverageWordLengthCharacters(), 0.01);
    }

    @Test
    void getAverageSentenceLengthCharacters() {
        Assertions.assertEquals(17.33, simpleTextAnalyzer.getAverageSentenceLengthCharacters(), 0.01);
    }

    @Test
    void getAverageSentenceLengthCharactersWithoutWhitespaces() {
        Assertions.assertEquals(14.8333, simpleTextAnalyzer.getAverageSentenceLengthCharactersWithoutWhitespaces(), 0.01);
    }

    @Test
    void getAverageSentenceLengthWords() {
        Assertions.assertEquals(deItemEarthAndSun.getAvgSentenceLengthWords(), simpleTextAnalyzer.getAverageSentenceLengthWords(), 0.01);
    }

    @Test
    void getSentenceCount() {
        Assertions.assertEquals(deItemEarthAndSun.getSentenceCount(), simpleTextAnalyzer.getSentenceCount());
    }

    @Test
    void getUniqueWords() {
        Assertions.assertEquals(19, simpleTextAnalyzer.getUniqueWords().size());
    }

    @Test
    void getLexicalDiversity() {
        Assertions.assertEquals(0.90, simpleTextAnalyzer.getLexicalDiversity(), 0.01);
    }

    @Test
    void getTextLength() {
        List<CharSequence> testParagraphs = new ArrayList<>(Arrays.asList(
                "Die Erde dreht sich um die Sonne. Wie lange dauert diese Umlaufzeit ca.?",
                "12 Stunden.",
                "1 Tag.",
                "3 Monate.",
                "1 Jahr."
        ));

        Assertions.assertEquals(105, simpleTextAnalyzer.getTextLength(testParagraphs));
    }
    @Test
    void getTextLengthWithoutWhiteSpaces() {
        List<CharSequence> testParagraphs = new ArrayList<>(Arrays.asList(
                "Die Erde dreht sich um die Sonne. Wie lange dauert diese Umlaufzeit ca.?",
                "12 Stunden.",
                "1 Tag.",
                "3 Monate.",
                "1 Jahr."
        ));

        Assertions.assertEquals(89, simpleTextAnalyzer.getTextLengthWithoutWhiteSpaces(testParagraphs));
    }

    @Test
    void longWordRatePercent() {
        Assertions.assertEquals(9.5238, simpleTextAnalyzer.longWordRatePercent(), 0.001);
    }

    @Test
    void lixReadabilityScore() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.lixReadabilityScore(), 0.01);
    }

}
