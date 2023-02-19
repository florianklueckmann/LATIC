package software.latic.connectives;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import software.latic.translation.Translation;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class BaseConnectivesTest {

    Properties props;

    @BeforeEach
    void setUp() throws IOException {
        Locale.setDefault(Locale.GERMAN);
        Translation.getInstance().setLocale(Locale.GERMAN);
        props = new Properties();
        props.load(IOUtils.readerFromString("StanfordCoreNLP-" + "german" + ".properties"));
    }

    static class TestDataClass {
        public TestDataClass(String testString, int expected, Properties props) {
            this.testDoc = new Document(props, testString);
            this.expected = expected;
        }

        public Document testDoc;
        public int expected;
    }



    @TestFactory
    Stream<DynamicTest> testConnectivesInDocumentRegex() {

        BaseConnectives tester = new BaseConnectives();

        TestDataClass[] data = new TestDataClass[]{
                new TestDataClass("Ich bin ein Baum", 0, props),
                new TestDataClass("Ehe ich ein Baum bin.", 1, props),
                new TestDataClass("IcH biN AlLerdings Ein BAUm.", 1, props),
                new TestDataClass("Ich bin allerdings allerdings ein Baum.", 2, props),
                new TestDataClass("Ich bin allerdings ein Baum allerdings ein Haus.", 2, props),
                new TestDataClass("Ich bin allerdings ein Baum allerdings und ein Haus.", 3, props),
                new TestDataClass("Ich bin allerdings ein Baum allerdings und ein Haus. Ich bin allerdings allerdings ein Baum.", 5, props),
                new TestDataClass("Ich bin allerdings ein Baum und ein Haus.", 2, props),
                new TestDataClass("Ich bin allerdings ein Baum und auch ein Haus.", 3, props),
                new TestDataClass("Ich bin allerdings dass ein Baum", 2, props),
                new TestDataClass("Ich bin allerdings sieht man dass ein Baum", 2, props),
                new TestDataClass("Egal wie groß du bist, ich bin größer.", 1, props),
                new TestDataClass("Ich bin teils Baum teils Haus.", 1, props),
                new TestDataClass("Entweder du bist ein Baum oder ein Haus.", 1, props),
                new TestDataClass("Du bist Halb HAlb Baum, halb Haus.", 1, props),
                new TestDataClass("Du bist halb halb Baum, halb Haus.", 1, props),
                new TestDataClass("Du bist halb Baum. Du bist halb Haus.", 0, props),
                new TestDataClass("Du bist zum einen ein Baum, zum anderen ein Haus.", 1, props),
                new TestDataClass("Du bist zum einen ein Baum. Zum anderen ein Haus.", 1, props),
                new TestDataClass("Du bist zum Beispiel ein Baum.", 1, props),
                new TestDataClass("Wie immer, du bist ein Baum.", 1, props),
                new TestDataClass("EinMAL war ein ein Baum. Ein aNDErmal ein Haus.", 1, props),
                new TestDataClass("Ich weiß übrigens, daß du hier bist.", 2, props),
                new TestDataClass("Der Baum, der hier steht ist schön.", 1, props)


        };

        return Arrays.stream(data).map(entry -> dynamicTest(
                String.format("%s contains %d connectives", entry.testDoc, entry.expected), () ->
                        assertEquals(entry.expected, tester.connectivesInDocument(entry.testDoc))
        ));
    }
}