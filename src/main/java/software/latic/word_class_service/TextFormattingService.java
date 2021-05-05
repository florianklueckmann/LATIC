package software.latic.word_class_service;

import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.Arrays;

public class TextFormattingService {

    ArrayList<String> puncts;
//    HashMap<String, String> abbrevDict;

    public TextFormattingService() {
        this.puncts = new ArrayList<>(Arrays.asList(".", ",", "?", "!", "(", ")", ":", ";", "'", "\"", "\\", "/", "-", "_"));
//        this.abbrevDict = new HashMap<>();
    }

    public ArrayList<String> getWordsWithoutPunctuations(Sentence sentence) {

        ArrayList<String> wordsWithoutPunctuations = new ArrayList<>(sentence.words());
        wordsWithoutPunctuations.removeAll(puncts);

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
