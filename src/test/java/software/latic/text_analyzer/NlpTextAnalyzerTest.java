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
    void givenDocumentWithEnglishSentence_whenNounPhrasesCountCalled_thenCorrectCountReturned() {
        // Arrange
        String[][] testCases = {
                {"The quick brown fox jumps over the lazy dog.", "2"},
                {"The quick brown fox jumps over the quick brown fox. The quick brown fox jumps over the quick brown fox.", "4"},
                {"The big apple is famous. New York is bustling. The nice house is green.", "3"},
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
    void givenDocumentWithPassiveVoice_whenPassiveConstructionsCountCalled_thenCorrectCountReturned() {
        // Arrange
        String[][] testCases = {
                {"", "0"},
                {"The class reads the book.", "0"},
                {"The book was read by the boy.", "1"},
                {"The homework was completed by the students, and the teacher was impressed.", "1"},
                {"The cake was baked by Alice. The homework was completed by the students.", "2"},
                {"The report was written by the intern, and the presentation was prepared by the team.", "2"},
                {"The decision was made.", "1"},
                {"The plan is being reviewed by the committee.", "1"},
                {"The report has been finalized by the committee.", "1"},
                {"The contract had been signed by both parties before the meeting was scheduled by the organizer.", "2"},
                {"The work will have been completed by next week.", "1"},
                {"Should the plan be approved by the board?", "1"},
                {"Was the issue resolved by the team or ignored by the manager?", "2"},
                {"The patient was examined by the doctor and was admitted by the nurse.", "2"},
                {"The system was designed by engineers, built by contractors, and tested by auditors.", "3"},
                {"It is believed by many that the policy was influenced by the lobbyists.", "2"},
                {"Rarely was the error detected by the old tool, but it was caught by the new one.", "2"},
                {"No changes were requested by the client, and the files were archived by the team.", "2"},
                {"Under strict supervision, the experiment was conducted by the researchers.", "1"},
                {"Every ticket was scanned by the device before the guests were seated by the ushers.", "2"},
        };

        for (String[] testCase : testCases) {
            String text = testCase[0];
            int expected = Integer.parseInt(testCase[1]);

            Document doc = new Document(text);
            NlpTextAnalyzer analyzer = NlpTextAnalyzer.getInstance();
            analyzer.setDoc(doc);

            // Act
            int result = analyzer.passiveConstructionsCount();

            // Assert
            assertEquals(expected, result, "Expected " + expected + " passive constructions in: " + text);
        }
    }
}