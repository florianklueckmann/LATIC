package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import static java.lang.Math.toIntExact;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleTextAnalyzer {

    Document doc;
//    ArrayList<CharSequence> paragraphs;

    //TODO Maybe use List<Sentence>?
    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    ArrayList<String> puncts;
    TextFormattingService textFormattingService;

    public SimpleTextAnalyzer(TextFormattingService textFormatter) {
        this.doc = doc;
        this.textFormattingService = textFormatter;
        this.puncts = new ArrayList<>(Arrays.asList(".", ",", "?", "!", "(", ")", ":", ";", "'", "\""));
    }

    protected boolean isPunctuation(String word) {
        return word.matches("([.,?!():;'\"])");
    }

    private int getWordCountSentence(Sentence sentence) {
        return toIntExact(sentence.words().stream().filter(w -> !isPunctuation(w)).count());
    }

    public int getWordCount() {
        return doc.sentences().stream().mapToInt(this::getWordCountSentence).sum();
    }

    //TODO We dont need this. GetCharCountWithWhiteSpace and WithoutWhiteSpace
    public int getTextCharCountWithoutPuncts() {
        var textCharCount = 0;
        for (Sentence sent : doc.sentences()) {
            for (var word : textFormattingService.getWordsWithoutPunctuations(sent)) {
                textCharCount += word.length();
            }

        }

        return textCharCount;
    }

    //TODO: Decide how to handle abbreviations (for example ca.)
    public double getAverageWordLengthCharacters() {
        return (double) getTextCharCountWithoutPuncts() / (double) getWordCount();
    }

    public int sentenceLengthWithoutWhitespaces(Sentence sentence) {
        return sentence.text().replace(" ", "").length();
    }

    public double getAverageSentenceLengthCharacters() {
        return (double) doc.sentences().stream().mapToInt(sent -> sent.text().length()).sum()
                / (double) getSentenceCount();
    }

    public double getAverageSentenceLengthCharactersWithoutWhitespaces() {

        return (double) doc.sentences().stream().mapToInt(sent -> sent.words().stream().mapToInt(String::length).sum()).sum()
                / (double) getSentenceCount();
    }

    //TODO Maybe for every sentence?
    public double getAverageSentenceLengthWords() {
        return (double) getWordCount() / (double) getSentenceCount();
    }

    //TODO Make sure to handle abbreviations in the middle of a sentence, like "There are a lot of people here e.g. me"
    //CoreNLP would split this into 3 sentences.
    public int getSentenceCount() {
        return toIntExact(doc.sentences().stream().filter(sentence -> sentence.length() > 1).count());
    }

    public HashSet<String> getUniqueWords() {
        HashSet<String> uniqueWords = new HashSet<>();

        for (var sent : doc.sentences()) {
            uniqueWords.addAll(textFormattingService.getWordsWithoutPunctuations(sent).stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList()));
        }

        return uniqueWords;
    }

    public double getLexicalDiversity() {
        return (double) getUniqueWords().size() / (double) getWordCount();
    }

    public int getTextLength(List<CharSequence> paragraphs) {
        return paragraphs.stream().mapToInt(CharSequence::length).sum();
    }

    public int getTextLengthWithoutWhiteSpaces(List<CharSequence> paragraphs) {
        return paragraphs.stream().mapToInt(charSequence -> charSequence.toString().replaceAll(" ", "").length()).sum();
    }

//    public double averageWordLengthCharacter() {
//        return (double) doc.sentences().stream()
//                .mapToInt(sentence -> sentence.words().stream()
//                        .mapToInt(word -> word.length()).sum()).sum() / (double) getWordCount();
//    }

    public double longWordRatePercent() {
        return (double) doc.sentences().stream()
                .mapToInt(sent -> toIntExact(sent.words().stream()
                        .filter(word -> word.length() > 6).count())).sum() / (double) getWordCount() * 100.0;
    }

    public double lixReadabilityScore() {
        return getAverageSentenceLengthWords() + longWordRatePercent();
    }
}
