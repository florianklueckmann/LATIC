package software.latic.text_analyzer;

import software.latic.helper.TagMapper;
import software.latic.item.TextItemData;
import software.latic.syllables.SyllableProvider;
import software.latic.task.TaskLevel;
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

    public static final SimpleTextAnalyzer instance = new SimpleTextAnalyzer();

    public static SimpleTextAnalyzer getInstance() {
        return instance;
    }

    Document doc;
    TextFormattingService textFormattingService = TextFormattingService.getInstance();
    int syllableCount = 0;

    private SimpleTextAnalyzer() {

    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
        this.syllableCount = 0;
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
                            || task.getId().toLowerCase().equals("lexicaldiversity")
                            || task.getLevel().equals(TaskLevel.TEXT_READABILITY)) {
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
        return textFormattingService.punctuationMarks.contains(word);
    }

    public int wordCount() {
        return doc.sentences().stream().mapToInt(this::sentenceWordCount).sum();
    }

    public int sentenceCount() {
        int sentenceCount = toIntExact(doc.sentences().stream().filter(sentence -> sentence.length() > 1).count());
        return sentenceCount > 0 ? sentenceCount : 1;
    }

    public int syllableCount() {
        if (syllableCount == 0) {
            syllableCount = SyllableProvider.getInstance().syllablesInDocument(doc);
        }
        return syllableCount;
    }

    private int sentenceWordCount(Sentence sentence) {
        return toIntExact(TagMapper.getInstance().replaceTags(sentence.tokens()).stream().filter(tag -> !tag.equals("PUNCT") && !tag.equals("POS")).count());
    }

    public double averageWordLengthCharacters() {
        return (double) textCountCharactersWithoutPunctuation() / (double) wordCount();
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

    public double averageSentenceLengthSyllables() {
       return (double) doc.sentences().stream()
               .mapToInt(sent -> sent.words().stream()
               .mapToInt(word -> SyllableProvider.getInstance().syllablesPerWord(word)).sum())
               .sum() / sentenceCount();
    }

    public double averageWordLengthSyllables() {
        return (double) syllableCount() / wordCount();
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

    public int wordsWithMoreThanTwoSyllables() {
        return SyllableProvider.getInstance().getWordsWithMoreThanTwoSyllables();
    }

    public int wordsWithMoreThanThreeSyllables() {
        return SyllableProvider.getInstance().getWordsWithMoreThanThreeSyllables();
    }

    public double wordsWithMoreThanTwoSyllablesPercent() {
        return ((double) wordsWithMoreThanTwoSyllables() / wordCount() * 100);
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

    //German indices
    public double fleschIndexGerman() {
        return 180 - averageSentenceLengthWords() - (58.5 * averageWordLengthSyllables());
    }

    public double wienerSachtextformel() {
        return 0.2656 * averageSentenceLengthWords() + 0.2744 * wordsWithMoreThanTwoSyllablesPercent() - 1.693;
    }

    public double gSMOG() {return Math.sqrt( (double) (wordsWithMoreThanTwoSyllables() * 30) / sentenceCount()) - 2;}

    //English indices
    public double fleschIndexEnglish() {
        return 206.835 - (1.015 * averageSentenceLengthWords()) - (84.6 * averageWordLengthSyllables());
    }

    public double fleschKincaid() {
        return ((0.39 * wordCount()) / sentenceCount()) + ((11.8 * syllableCount()) / wordCount()) - 15.59;
    }

    public double gunningFog() {
        return 0.4 * wordCount() / sentenceCount() + (double) wordsWithMoreThanTwoSyllables() / wordCount();
    }

    public double automatedReadabilityIndex () {
        return 4.71 * textCountCharactersWithoutPunctuation() /  wordCount() + 0.5 *wordCount() / sentenceCount() - 21.43;
    }

    public double colemanLiau () { return 0.0588 * ( (double) textCountCharactersWithoutPunctuation() / (double) wordCount() * 100 ) - 0.296 * ( (double)sentenceCount() / (double) wordCount() * 100 ) - 15.8; }

    public double SMOG () { return 1.043 * Math.sqrt(30 *  (double) wordsWithMoreThanTwoSyllables() / (double) sentenceCount() + 3.1291); }
}
