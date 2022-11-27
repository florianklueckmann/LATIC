package software.latic.connectives;

import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class BaseConnectivesTest {

    @BeforeEach
    void setUp() {
        Locale.setDefault(Locale.GERMAN);
    }

    static class TestDataClass {
        public TestDataClass(String testString, int expected) {
            this.testString = testString;
            this.expected = expected;
        }

        public String testString;
        public int expected;
    }

    @TestFactory
    Stream<DynamicTest> testConnectivesInDocument() {
        var tester = new BaseConnectives();

        TestDataClass[] data = new TestDataClass[]{
                new TestDataClass("Ich bin ein Baum", 0),
                new TestDataClass("Ich bin allerdings ein Baum.", 1),
                new TestDataClass("Ich bin allerdings allerdings ein Baum.", 1),
                new TestDataClass("Ich bin allerdings ein Baum allerdings ein Haus.", 2),
                new TestDataClass("Ich bin allerdings ein Baum allerdings und ein Haus.", 2),
                new TestDataClass("Ich bin allerdings ein Baum allerdings und ein Haus. Ich bin allerdings allerdings ein Baum.", 3),
                new TestDataClass("Ich bin allerdings ein Baum und ein Haus.", 2),
                new TestDataClass("Ich bin allerdings ein Baum und auch ein Haus.", 2),
                new TestDataClass("Ich bin allerdings dass ein Baum", 1),
                new TestDataClass("Ich bin allerdings sieht man dass ein Baum", 2)
        };
        return Arrays.stream(data).map(entry -> dynamicTest(
                String.format("%s contains %d connectives", entry.testString, entry.expected), () ->
                        assertEquals(entry.expected, tester.connectivesInDocument(new Document(entry.testString)))
        ));
    }
}