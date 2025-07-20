package software.latic.text_analyzer;

import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NlpTextAnalyzerTest {

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