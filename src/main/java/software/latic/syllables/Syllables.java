package software.latic.syllables;

import edu.stanford.nlp.simple.Document;

public interface Syllables {
    Syllables syllables = null;
    static Syllables getInstance() {
        return syllables;
    }

    int syllablesPerWord(String word);
    int syllablesInDocument(Document doc);

    int getWordsWithMoreThanTwoSyllables();
    int getWordsWithMoreThanThreeSyllables();
}
