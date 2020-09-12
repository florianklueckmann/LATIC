package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import static java.lang.Math.toIntExact;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleTextAnalyzer {

    Document doc;
    //ArrayList<CharSequence> paragraphs;
    ArrayList<String> puncts;
    TextFormattingService textFormattingService;

    public SimpleTextAnalyzer(TextFormattingService textFormatter) {
        this.textFormattingService = textFormatter;
        this.puncts = new ArrayList<>(Arrays.asList(".", ",", "?", "!", "(", ")", ":", ";", "'", "\""));
    }

    public Document getDoc() {
        //TODO Maybe use List<Sentence>?
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    protected boolean isPunctuation(String word) {
        return word.matches("([.,?!():;'\"])");
    }

    private int sentenceWordCount(Sentence sentence) {
        return toIntExact(sentence.words().stream().filter(w -> !isPunctuation(w)).count());
    }

    public int wordCount() {
        return doc.sentences().stream().mapToInt(this::sentenceWordCount).sum();
    }

    public double averageWordLengthCharacters() {
        //TODO: Decide how to handle abbreviations (for example ca.)
        return (double) textCountCharactersWithoutPunctuation() / (double) wordCount();
    }

    public int sentenceCount() {
        //TODO Make sure to handle abbreviations in the middle of a sentence, like "There are a lot of people here e.g. me"
        //CoreNLP would split this into 3 sentences.
        return toIntExact(doc.sentences().stream().filter(sentence -> sentence.length() > 1).count());
    }

    public int sentenceLengthWithoutWhitespaces(Sentence sentence) {
        return sentence.text().replace(" ", "").length();
    }

    public double averageSentenceLengthCharacters() {
        return (double) doc.sentences().stream().mapToInt(sent -> sent.text().length()).sum()
                / (double) sentenceCount();
    }

    public double averageSentenceLengthCharactersWithoutWhitespaces() {

        return (double) doc.sentences().stream().mapToInt(sent -> sent.words().stream().mapToInt(String::length).sum()).sum()
                / (double) sentenceCount();
    }

    public double averageSentenceLengthWords() {
        //TODO Maybe for every sentence?
        return (double) wordCount() / (double) sentenceCount();
    }

    protected int textCountCharactersWithoutPunctuation() {
        //TODO We dont need this. GetCharCountWithWhiteSpace and WithoutWhiteSpace
        var textCharCount = 0;
        for (Sentence sent : doc.sentences()) {
            for (var word : textFormattingService.getWordsWithoutPunctuations(sent)) {
                textCharCount += word.length();
            }

        }

        return textCharCount;
    }

    public HashSet<String> uniqueWords() {
        HashSet<String> uniqueWords = new HashSet<>();

        for (var sent : doc.sentences()) {
            uniqueWords.addAll(textFormattingService.getWordsWithoutPunctuations(sent).stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList()));
        }

        return uniqueWords;
    }

    public double lexicalDiversity() {
        return (double) uniqueWords().size() / (double) wordCount();
    }

    public int textLengthCharacters(List<CharSequence> paragraphs) {
        return paragraphs.stream().mapToInt(CharSequence::length).sum();
    }

    public int textLengthCharactersWithoutWhiteSpaces(List<CharSequence> paragraphs) {
        return paragraphs.stream().mapToInt(charSequence -> charSequence.toString().replaceAll(" ", "").length()).sum();
    }

    public double longWordRatePercent() {
        return (double) doc.sentences().stream()
                .mapToInt(sent -> toIntExact(sent.words().stream()
                        .filter(word -> word.length() > 6).count())).sum() / (double) wordCount() * 100.0;
    }

    public double lixReadabilityScore() {
        return averageSentenceLengthWords() + longWordRatePercent();
    }
}
