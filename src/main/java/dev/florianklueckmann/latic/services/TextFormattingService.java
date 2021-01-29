package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

public class TextFormattingService {

    ArrayList<String> puncts;
    HashMap<String, String> abbrevDict;

    public TextFormattingService() {
        this.puncts = new ArrayList<>(Arrays.asList(".", ",", "?", "!", "(", ")", ":", ";", "'", "\"", "\\", "/", "-", "_"));
        this.abbrevDict = new HashMap<>();
    }

    //TODO I believe we should overwrite Sentence
    protected ArrayList<String> getWordsWithoutPunctuations(Sentence sentence) {

        ArrayList<String> wordsWithoutPunctuations = new ArrayList<>(sentence.words());
        wordsWithoutPunctuations.removeAll(puncts);

        return wordsWithoutPunctuations;

    }

//    protected boolean isPunctuation(String word) {
//        return word.matches("([.,?!():;'\"])");
//    }


//    protected boolean hasAbbreviations(String input) {
//        return false; //TODO
//    }

    public String replaceAbbreviations(String input) {
        return abbrevDict.entrySet().stream()
                .map(currentAbbreviationEntry -> (Function<String, String>) text ->
                        text.replace(currentAbbreviationEntry.getKey(), currentAbbreviationEntry.getValue()))
                .reduce(Function.identity(), Function::andThen)
                .apply(input);
    }

}
