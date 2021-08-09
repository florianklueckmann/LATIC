package software.latic.word_class_service;

import software.latic.TestItem;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

class TextFormattingServiceTest {

    private Properties props = new Properties();
    private Document doc;

    private TextFormattingService textFormattingService;

    //TODO Mock the TextFormatting?
    //TextFormattingService mockedTextFormattingService = mock(TextFormattingService.class);

    private TestItem deItemEarthAndSun = new TestItem(
            "Wie lange dauert diese Umlaufzeit ca.?",
            6, 1, 6, 123, 123, 4.3);

    @BeforeEach
    void setUp() {
        try {
            props.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
        } catch (IOException e) {
            //TODO: Error Handling
            e.printStackTrace();
        }

        doc = new Document(props, deItemEarthAndSun.getText());
        textFormattingService = new TextFormattingService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getWordsWithoutPunctuations() {
        var wordsWithoutPuncts = textFormattingService.getWordsWithoutPunctuations(doc.sentence(0));

        Assertions.assertEquals(6, wordsWithoutPuncts.size());
        Assertions.assertFalse(wordsWithoutPuncts.contains("."));
        Assertions.assertFalse(wordsWithoutPuncts.contains("?"));
    }
}
