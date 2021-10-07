package software.latic.syllables;

import edu.stanford.nlp.simple.Document;
import javafx.collections.FXCollections;

import java.util.List;

public abstract class BaseSyllables implements Syllables {

    int wordsWithMoreThanTwoSyllables = 0;
    int wordsWithMoreThanThreeSyllables = 0;

    @Override
    public int syllablesInDocument(Document doc) {
        var docSyllableCount = 0;
        wordsWithMoreThanTwoSyllables = 0;
        wordsWithMoreThanThreeSyllables = 0;
        var sentences = doc.sentences();
        List<String> words = FXCollections.observableArrayList();

        for (var sentence : sentences) {
            words.addAll(sentence.words().stream().filter(word -> !word.matches("\\W")).toList());
        }

        for (var word : words) {
            var count = syllablesPerWord(word);
            if (count > 2) {
                wordsWithMoreThanTwoSyllables++;
            }
            if (count > 3) {
                wordsWithMoreThanThreeSyllables++;
            }
            docSyllableCount += count;
        }

        return docSyllableCount;
    }

    @Override
    public int getWordsWithMoreThanTwoSyllables() {
        return wordsWithMoreThanTwoSyllables;
    }

    @Override
    public int getWordsWithMoreThanThreeSyllables() {
        return wordsWithMoreThanThreeSyllables;
    }
}
