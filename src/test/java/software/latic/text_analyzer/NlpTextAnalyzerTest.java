package software.latic.text_analyzer;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.Test;
import software.latic.syllables.SyllableProvider;
import software.latic.translation.Translation;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class NlpTextAnalyzerTest {

    NlpTextAnalyzer nlpTextAnalyzer;

    void setDocument(String locale, String text) {
        Properties props = new Properties();

        if (locale.equalsIgnoreCase("de")) {
            try {
                props.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Translation.getInstance().setLocale(Locale.GERMAN);
        } else {
            Translation.getInstance().setLocale(Locale.ENGLISH);
        }


        Document doc = new Document(props, text);
        nlpTextAnalyzer = NlpTextAnalyzer.getInstance();
        nlpTextAnalyzer.setDoc(doc);
    }

    @Test
    void givenDocumentWithPassiveVoice_whenPassiveConstructionsCalled_thenCorrectDependenciesReturned() {
        // Arrange
        String text = "The book was read by the class.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        String result = analyzer.passiveConstructions();

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.contains("nsubj:pass"), "Expected 'nsubj:pass' dependency for passive voice.");
        assertTrue(result.contains("aux:pass"), "Expected 'aux:pass' dependency for auxiliary passive.");
        assertTrue(result.contains("obl"), "Expected 'obl' dependency to indicate 'by the class'.");
    }

    @Test
    void givenDocumentWithSimpleSentence_whenNounPhrasesCountCalled_thenCorrectCountReturned() {
        // Arrange
        String text = "The quick brown fox jumps over the lazy dog.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.nounPhrasesCount();

        // Assert
        assertEquals(2, result, "Expected 2 noun phrases in the sentence.");
    }

    @Test
    void givenDocumentWithRepeatedPhrase_whenNounPhrasesCountCalled_thenCorrectCountReturned() {
        // Arrange
        String text = "The quick brown fox jumps over the quick brown fox. The quick brown fox jumps over the quick brown fox.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.nounPhrasesCount();

        // Assert
        assertEquals(4, result, String.format("Expected 2 noun phrases, but got %d in the sentence %s", result, text));
    }

    @Test
    void givenDocumentWithEnglishSentence_whenNounPhrasesCountCalled_thenCorrectCountReturned() {
        // Arrange
        String[][] testCases = {
                {"The dog barked loudly.", "1"},
                {"She gave a gift to her brother.", "3"},
                {"That small red book belongs to Emily.", "2"},
                {"I saw a group of children playing outside.", "2"},
                {"The man with the briefcase entered the room.", "2"},
                {"A surprisingly large number of tourists arrived last year.", "2"},
                {"The teacher who inspired me most retired last year.", "2"},
                {"Many of the students in the back row were sleeping.", "1"},
                {"Her decision to quit her job so suddenly shocked everyone.", "3"},
                {"The man with the hat from the old shop is here.", "1"},
        };

        for (String[] testCase : testCases) {
            String text = testCase[0];
            int expected = Integer.parseInt(testCase[1]);

            Document doc = new Document(text);
            setDocument("en", text);

            // Act
            int result = nlpTextAnalyzer.nounPhrasesCount();

            // Assert
            assertEquals(expected, result, "Expected " + expected + " noun phrases in: " + text);
        }
    }

    @Test
    void givenDocumentWithGermanSentence_whenNounPhrasesCountCalled_thenCorrectCountReturned() {
        // Arrange
        String[][] testCases = {
                {"Der schnelle braune Fuchs springt über den faulen Hund.", "2"},
                {"Schnelle braune Füche springen über faule Hunde.", "2"},
                {"Der Mann geht in das Haus.", "2"},
                {"Die schöne Blume blüht im Garten.", "2"},
                {"Viele Schüler arbeiten mit großer Konzentration.", "2"},
                {"Blumen blühen in großen Gärten.", "1"},
                {"Blumen blühen in Hamburg.", "1"},
                {"In der Elbchaussee wohnt ein Schnösel.", "2"},
                {"Auf St. Pauli brennt noch Licht.", "1"},
                {"Seit gestern bin ich 58.", "0"},
                {"Viele der Schüler aus der letzten Reihe haben nichts verstanden.", "1"},
                {"Liegt der Zettel in deiner neuen Mappe oder in deiner Alten?", "3"},
                {"Ein Glas Wasser steht auf dem Tisch neben dem Buch.", "3"},
                {"Der braune Bär macht seinen Winterschalf.", "2"},
                {"Ich traf eine Gruppe von Touristen am Bahnhof.", "2"}
        };

        for (String[] testCase : testCases) {
            String text = testCase[0];
            int expected = Integer.parseInt(testCase[1]);

            Document doc = new Document(text);
            setDocument("de", text);

            // Act
            int result = nlpTextAnalyzer.nounPhrasesCount();

            // Assert
            assertEquals(expected, result, "Expected " + expected + " noun phrases in: " + text);
        }
    }

    @Test
    void givenEmptyDocument_whenNounPhrasesCountCalled_thenZeroCountReturned() {
        // Arrange
        String text = "";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.nounPhrasesCount();

        // Assert
        assertEquals(0, result, "Expected 0 noun phrases in an empty document.");
    }

    @Test
    void givenDocumentWithMultipleSentences_whenNounPhrasesCountCalled_thenCorrectCountReturned() {
        // Arrange
        String text = "The big apple is famous. New York is bustling. The nice house is green.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.nounPhrasesCount();

        // Assert
        assertEquals(3, result, "Expected 3 noun phrases across two sentences.");
    }

    @Test
    void givenDocumentWithComplexStructure_whenNounPhrasesCountCalled_thenCorrectCountReturned() {
        // Arrange
        String text = "The teacher, who was highly respected, gave the students an insightful lecture.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.nounPhrasesCount();

        // Assert
        assertEquals(4, result, "Expected 4 noun phrases in the complex sentence.");
    }

    @Test
    void givenDocumentWithoutPassiveVoice_whenPassiveConstructionsCalled_thenMinimalDependenciesReturned() {
        // Arrange
        String text = "The class reads the book.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        String result = analyzer.passiveConstructions();

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertFalse(result.contains("nsubj:pass"), "Result should not contain 'nsubj:pass'.");
        assertFalse(result.contains("aux:pass"), "Result should not contain 'aux:pass'.");
    }

    @Test
    void givenEmptyDocument_whenPassiveConstructionsCalled_thenEmptyStringReturned() {
        // Arrange
        String text = "";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        String result = analyzer.passiveConstructions();

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals("", result, "Expected an empty string for an empty document.");
    }

    @Test
    void givenDocumentWithMultipleSentences_whenPassiveConstructionsCalled_thenDependenciesForAllSentencesReturned() {
        // Arrange
        String text = "The cake was baked by Alice. The cat chased the mouse.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        String result = analyzer.passiveConstructions();

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.contains("nsubj:pass"), "Expected 'nsubj:pass' for sentence with passive voice.");
        assertTrue(result.contains("aux:pass"), "Expected 'aux:pass' for sentence with passive voice.");
        assertTrue(result.contains("nsubj"), "Expected 'nsubj' for sentence without passive voice.");
    }

    @Test
    void givenDocumentWithComplexConstructions_whenPassiveConstructionsCalled_thenCorrectDependenciesReturned() {
        // Arrange
        String text = "The homework was completed by the students, and the teacher was impressed.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        String result = analyzer.passiveConstructions();

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.contains("nsubj:pass"), "Expected 'nsubj:pass' for passive construction.");
        assertTrue(result.contains("aux:pass"), "Expected 'auxp:ass' for passive auxiliary.");
        assertTrue(result.contains("cc"), "Expected 'cc' for conjunction between clauses.");
    }

    @Test
    void givenDocumentWithPassiveVoice_whenPassiveConstructionsCountCalled_thenCorrectCountReturned() {
        // Arrange
        String text = "The book was read by the boy.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.passiveConstructionsCount();

        // Assert
        assertEquals(1, result, "Expected 1 passive construction.");
    }

    @Test
    void givenDocumentWithoutPassiveVoice_whenPassiveConstructionsCountCalled_thenZeroCountReturned() {
        // Arrange
        String text = "The boy reads the book.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.passiveConstructionsCount();

        // Assert
        assertEquals(0, result, "Expected 0 passive constructions.");
    }

    @Test
    void givenEmptyDocument_whenPassiveConstructionsCountCalled_thenZeroCountReturned() {
        // Arrange
        String text = "";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.passiveConstructionsCount();

        // Assert
        assertEquals(0, result, "Expected 0 passive constructions for an empty document.");
    }

    @Test
    void givenDocumentWithMultiplePassiveVoiceSentences_whenPassiveConstructionsCountCalled_thenCorrectCountReturned() {
        // Arrange
        String text = "The cake was baked by Alice. The homework was completed by the students.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.passiveConstructionsCount();

        // Assert
        assertEquals(2, result, "Expected 2 passive constructions.");
    }

    @Test
    void givenDocumentWithMultiplePassiveVoiceInOneSentence_whenPassiveConstructionsCountCalled_thenCorrectCountReturned() {
        // Arrange
        String text = "The report was written by the intern, and the presentation was prepared by the team.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.passiveConstructionsCount();

        // Assert
        assertEquals(2, result, "Expected 2 passive constructions.");
    }

    @Test
    void givenDocumentWithComplexPassives_whenPassiveConstructionsCountCalled_thenCorrectCountReturned() {
        // Arrange
        String text = "The homework was completed by the students, and the teacher was impressed.";
        Document doc = new Document(text);
        NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        // Act
        int result = analyzer.passiveConstructionsCount();

        // Assert
        assertEquals(1, result, "Expected 1 passive construction despite the complex structure.");
    }
}