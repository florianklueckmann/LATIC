package software.latic;

import org.junit.jupiter.api.Test;
import software.latic.translation.Translation;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrimaryModelTest {

    @Test
    void joinHyphenatedLineBreaks_whenEnabled_mergesHyphenSplitWords() {
        Translation.getInstance().setLocale(Locale.ENGLISH);
        PrimaryModel model = new PrimaryModel();
        model.setJoinHyphenatedLineBreaks(true);

        model.initializeDocument(List.<CharSequence>of("This is a hy- ", " phenated word."));

        String text = model.getDoc().text();
        assertFalse(text.contains("-\n"), "hyphen+newline should be removed");
        assertTrue(text.contains("hyphenated"), "split word should be merged: " + text);
    }

    @Test
    void joinHyphenatedLineBreaks_whenEnabled_handlesEmbeddedLineBreaks() {
        Translation.getInstance().setLocale(Locale.ENGLISH);
        PrimaryModel model = new PrimaryModel();
        model.setJoinHyphenatedLineBreaks(true);

        model.initializeDocument(List.<CharSequence>of("They play SCHOKOLADEN-\r\nSPIEL."));

        String text = model.getDoc().text();
        assertTrue(text.contains("SCHOKOLADENSPIEL"), "split word should be merged: " + text);
        assertFalse(text.contains("SCHOKOLADEN-"), "line-break hyphen should be removed: " + text);
    }

    @Test
    void joinHyphenatedLineBreaks_whenDisabled_preservesHyphen() {
        Translation.getInstance().setLocale(Locale.ENGLISH);
        PrimaryModel model = new PrimaryModel();
        model.setJoinHyphenatedLineBreaks(false);

        model.initializeDocument(List.<CharSequence>of("This is a hy-", "phenated word."));

        String text = model.getDoc().text();
        assertTrue(text.contains("hy-"), "hyphen should remain when option disabled: " + text);
    }
}
