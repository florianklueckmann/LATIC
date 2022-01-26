package software.latic.word_class_service;

import edu.stanford.nlp.simple.Sentence;
import software.latic.translation.Translation;

import java.util.ArrayList;
import java.util.Arrays;

public class TextFormattingService {

    private static final TextFormattingService instance = new TextFormattingService();

    public static TextFormattingService getInstance() {
        return instance;
    }

    private TextFormattingService() {

    }

    public final ArrayList<String> punctuationMarks =
            new ArrayList<>(Arrays.asList(".", ",", "?", "!", "(", ")", ":", ";", "'", "\"", "„", "“", "\\", "/", "-", "_"));

    public ArrayList<String> getWordsWithoutPunctuations(Sentence sentence) {

        ArrayList<String> wordsWithoutPunctuations = new ArrayList<>(sentence.words());
        wordsWithoutPunctuations.removeAll(punctuationMarks);

        return wordsWithoutPunctuations;
    }

//INFO: We plan to handle abbreviations in a future release
//    public String replaceAbbreviations(String input) {
//        return abbrevDict.entrySet().stream()
//                .map(currentAbbreviationEntry -> (Function<String, String>) text ->
//                        text.replace(currentAbbreviationEntry.getKey(), currentAbbreviationEntry.getValue()))
//                .reduce(Function.identity(), Function::andThen)
//                .apply(input);
//    }

}
