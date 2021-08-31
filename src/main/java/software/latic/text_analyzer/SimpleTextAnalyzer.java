package software.latic.text_analyzer;

import software.latic.item.TextItemData;
import software.latic.word_class_service.TextFormattingService;
import software.latic.task.Task;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import javafx.collections.ObservableList;

import static java.lang.Math.toIntExact;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleTextAnalyzer implements TextAnalyzer {

    Document doc;
    ArrayList<String> puncts;
    TextFormattingService textFormattingService;

    public SimpleTextAnalyzer(TextFormattingService textFormatter) {
        this.textFormattingService = textFormatter;
        this.puncts = new ArrayList<>(Arrays.asList(".", ",", "?", "!", "(", ")", ":", ";", "'", "\"", "„", "“", "-"));
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }


    public void processTasks(TextItemData textItemData, ObservableList<Task> tasks) {
        setDoc(doc);

        for (var task : tasks) {
            if (task.selectedProperty().get()) {
                java.lang.reflect.Method setter;
                java.lang.reflect.Method simpleTextAnalyzerMethod;

                var setterName = task.getId();
                setterName = setterName.substring(0, 1).toUpperCase() + setterName.substring(1);
                setterName = "set" + setterName;

                try {
                    simpleTextAnalyzerMethod = this.getClass().getMethod(task.getId());

                    if (task.getId().toLowerCase().contains("average")
                            || task.getId().toLowerCase().contains("score")
                            || task.getId().toLowerCase().equals("lexicaldiversity")) {
                        setter = textItemData.getClass().getMethod(setterName, double.class);
                        setter.invoke(textItemData, (double) simpleTextAnalyzerMethod.invoke(this));
                    } else if (task.getId().toLowerCase().contains("count")) {
                        setter = textItemData.getClass().getMethod(setterName, int.class);
                        setter.invoke(textItemData, (int) simpleTextAnalyzerMethod.invoke(this));
                    } else {
                        setter = textItemData.getClass().getMethod(setterName, String.class);
                        setter.invoke(textItemData, String.valueOf(simpleTextAnalyzerMethod.invoke(this)));
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected boolean isPunctuation(String word) {
        return puncts.contains(word);
    }

    private int sentenceWordCount(Sentence sentence) {
        return toIntExact(sentence.words().stream().filter(w -> !isPunctuation(w)).count());
    }

    public int wordCount() {
        return doc.sentences().stream().mapToInt(this::sentenceWordCount).sum();
    }

    public double averageWordLengthCharacters() {
        return (double) textCountCharactersWithoutPunctuation() / (double) wordCount();
    }

    public int sentenceCount() {
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
        return (double) wordCount() / (double) sentenceCount();
    }

    public int textCountCharactersWithoutPunctuation() {
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
