package software.latic.word_class_service;

import software.latic.TestItem;
import software.latic.syllables.SyllableProvider;
import software.latic.syllables.Syllables;
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

    private final String deItemTwo = "Es war einmal ein Junge. Er lebte mit seinen Eltern im Walde. Eines Tages beobachtete der Junge ein Eichhörnchen, das gerade eine Nuss vergrub. „Das Eichhörnchen macht sich bestimmt bereit für den Winterschlaf“, dachte der Junge.";

    private final String enItemTwo = "Deoxyribonucleic acid is a molecule composed of two polynucleotide chains that coil around each other to form a double helix carrying genetic instructions for the development, functioning, growth and reproduction of all known organisms and many viruses. Deoxyribonucleic acid and ribonucleic acid are nucleic acids.";

    private final List<CharSequence> paragraphs = new ArrayList<>(
            Arrays.asList(
                    "Die Erde dreht sich um die Sonne. Wie lange dauert diese Umlaufzeit ca.?",
                    "12 Stunden.",
                    "1 Tag.",
                    "3 Monate.",
                    "1 Jahr."));

    SimpleTextAnalyzer simpleTextAnalyzer;

    void setDocument(String locale, String text) {
        Properties props = new Properties();

        if (locale.equalsIgnoreCase("de")) {
            try {
                props.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
            } catch (IOException e) {
                //TODO: Error Handling
                e.printStackTrace();
            }

            Translation.getInstance().setLocale(Locale.GERMAN);
        } else {
            Translation.getInstance().setLocale(Locale.ENGLISH);
        }


        Document doc = new Document(props, text);
        simpleTextAnalyzer = SimpleTextAnalyzer.getInstance();
        simpleTextAnalyzer.setDoc(doc);
        SyllableProvider.getInstance().syllablesInDocument(doc);
    }

    @BeforeEach
    void setUp() {
        setDocument("de", deItemEarthAndSun.getText());
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
        setDocument("de", deItemTwo);
        Assertions.assertEquals(68.63, simpleTextAnalyzer.fleschIndexGerman(), 0.01);
    }

    @Test
    void wienerSachtextformel() {
        setDocument("de", deItemTwo);
        Assertions.assertEquals(4.51, simpleTextAnalyzer.wienerSachtextformel(), 0.01);
    }

    @Test
    void gSMOG() {
        setDocument("de", deItemTwo);
        Assertions.assertEquals(0.7386, simpleTextAnalyzer.gSMOG(), 0.01);
    }

    @Test
    void fleschIndexEnglish() {
        setDocument("en", enItemTwo);
        Assertions.assertEquals(-3.81, simpleTextAnalyzer.fleschIndexEnglish(), 0.01);
    }

    @Test
    void fleschKincaid() {
        setDocument("en", enItemTwo);
        Assertions.assertEquals(19.41, simpleTextAnalyzer.fleschKincaid(), 0.01);
    }

    @Test
    void gunningFog() {
        setDocument("en", enItemTwo);
        Assertions.assertEquals(9.124, simpleTextAnalyzer.gunningFog(), 0.01);
    }

    @Test
    void automatedReadabilityIndex() {
        setDocument("en", enItemTwo);
        Assertions.assertEquals(17.77, simpleTextAnalyzer.automatedReadabilityIndex(), 0.01);
    }

    @Test
    void colemanLiau() {
        setDocument("en", enItemTwo);
        Assertions.assertEquals(17.77, simpleTextAnalyzer.colemanLiau(), 0.01);
    }

    @Test
    void SMOG() {
        setDocument("en", enItemTwo);
        Assertions.assertEquals(15.23, simpleTextAnalyzer.SMOG(), 0.01);
    }

}
