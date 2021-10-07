package software.latic.word_class_service;

import software.latic.TestItem;
import software.latic.text_analyzer.SimpleTextAnalyzer;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.*;
import software.latic.translation.Translation;

import java.io.IOException;
import java.util.*;

class SimpleTextAnalyzerTest {

    //TODO Mock the TextFormatting?
    //TextFormattingService mockedTextFormattingService = mock(TextFormattingService.class);

    private final TestItem deItemEarthAndSun = new TestItem(
            "Die Erde dreht sich um die Sonne. Wie lange dauert diese Umlaufzeit ca.?\n" +
                    "12 Stunden.\n" +
                    "1 Tag.\n" +
                    "3 Monate.\n" +
                    "1 Jahr.",
            21, 6, 27, 3.5, 3.90,
            13.66, 4.5, 1.28);

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

        Translation.getInstance().setLocale(Locale.GERMAN);

        Document doc = new Document(props, deItemEarthAndSun.getText());
        simpleTextAnalyzer = new SimpleTextAnalyzer(new TextFormattingService());
        simpleTextAnalyzer.setDoc(doc);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getWordCount() {
        Assertions.assertEquals(deItemEarthAndSun.getWordCount(), simpleTextAnalyzer.wordCount());
    }

    @Test
    void getSyllableCount() {
        Assertions.assertEquals(deItemEarthAndSun.getSyllableCount(), simpleTextAnalyzer.syllableCount());
    }

    @Test
    void getTextCharCountWithoutPuncts() {
        Assertions.assertEquals(82, simpleTextAnalyzer.textCountCharactersWithoutPunctuation());
    }

    @Test
    void getAverageWordLengthCharacters() {
        Assertions.assertEquals(deItemEarthAndSun.getAvgWordLengthChar(), simpleTextAnalyzer.averageWordLengthCharacters(), 0.01);
    }

    @Test
    void getAverageSentenceLengthCharacters() {
        Assertions.assertEquals(17.33, simpleTextAnalyzer.averageSentenceLengthCharacters(), 0.01);
    }

    @Test
    void getAverageSentenceLengthCharactersWithoutWhitespaces() {
        Assertions.assertEquals(14.8333, simpleTextAnalyzer.averageSentenceLengthCharactersWithoutWhitespaces(), 0.01);
    }

    @Test
    void getAverageSentenceLengthWords() {
        Assertions.assertEquals(deItemEarthAndSun.getAvgSentenceLengthWords(), simpleTextAnalyzer.averageSentenceLengthWords(), 0.01);
    }

    @Test
    void averageSentenceLengthSyllables() {
        Assertions.assertEquals(deItemEarthAndSun.getAverageSentenceLengthSyllables(), simpleTextAnalyzer.averageSentenceLengthSyllables(), 0.01);
    }

    @Test
    void averageWordLengthSyllables() {
        Assertions.assertEquals(deItemEarthAndSun.getAvgWordLengthSyll(), simpleTextAnalyzer.averageWordLengthSyllables(), 0.01);
    }

    @Test
    void getSentenceCount() {
        Assertions.assertEquals(deItemEarthAndSun.getSentenceCount(), simpleTextAnalyzer.sentenceCount());
    }

    @Test
    void getUniqueWords() {
        Assertions.assertEquals(19, simpleTextAnalyzer.uniqueWords().size());
    }

    @Test
    void getLexicalDiversity() {
        Assertions.assertEquals(0.90, simpleTextAnalyzer.lexicalDiversity(), 0.01);
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

        Assertions.assertEquals(105, simpleTextAnalyzer.textLengthCharacters(testParagraphs));
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

        Assertions.assertEquals(89, simpleTextAnalyzer.textLengthCharactersWithoutWhiteSpaces(testParagraphs));
    }

    @Test
    void longWordRatePercent() {
        Assertions.assertEquals(9.5238, simpleTextAnalyzer.longWordRatePercent(), 0.001);
    }

    @Test
    void lixReadabilityScore() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.lixReadabilityScore(), 0.01);
    }


    @Test
    void fleschIndexGerman() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.fleschIndexGerman(), 0.01);
    }

    @Test
    void wienerSachtextformel() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.wienerSachtextformel(), 0.01);
    }

    @Test
    void gSMOG() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.gSMOG(), 0.01);
    }

    @Test
    void fleschIndexEnglish() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.fleschIndexEnglish(), 0.01);
    }

    @Test
    void fleschKincaid() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.fleschKincaid(), 0.01);
    }

    @Test
    void gunningFog() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.gunningFog(), 0.01);
    }

    @Test
    void automatedReadabilityIndex() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.automatedReadabilityIndex(), 0.01);
    }

    @Test
    void colemanLiau() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.colemanLiau(), 0.01);
    }

    @Test
    void SMOG() {
        Assertions.assertEquals(13.0238, simpleTextAnalyzer.SMOG(), 0.01);
    }

}
