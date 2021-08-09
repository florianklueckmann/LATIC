package software.latic.syllables;

public interface Syllables {
    Syllables syllables = null;
    static Syllables getInstance() {
        return syllables;
    }

    int syllablesPerWord(String word);
}
