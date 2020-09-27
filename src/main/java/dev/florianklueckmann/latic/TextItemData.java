package dev.florianklueckmann.latic;

import javafx.beans.property.*;
import javafx.collections.ObservableMap;

public class TextItemData {
    StringProperty text;
    StringProperty textAndPosTags;
    StringProperty posTagsPerSentence;
    IntegerProperty wordCount;
    DoubleProperty averageWordLengthChar;
    IntegerProperty sentenceCount;
    DoubleProperty averageSentenceLengthCharacters;
    DoubleProperty averageSentenceLengthCharactersWithoutWhitespaces;
    DoubleProperty averageSentenceLengthWords;
    MapProperty<String, Integer> posTagCount;
    public TextItemData(String text,
                    String textAndPosTags,
                    String posTagsPerSentence,
                    Integer wordCount,
                    Double averageWordLengthChar,
                    Integer sentenceCount,
                    Double averageSentenceLengthCharacters,
                    Double averageSentenceLengthCharactersWithoutWhitespaces,
                    Double averageSentenceLengthWords,
                    ObservableMap<String, Integer> posTagCount) {
        this.text = new SimpleStringProperty(text);
        this.textAndPosTags = new SimpleStringProperty(textAndPosTags);
        this.posTagsPerSentence = new SimpleStringProperty(posTagsPerSentence);
        this.wordCount = new SimpleIntegerProperty(wordCount);
        this.averageWordLengthChar = new SimpleDoubleProperty(averageWordLengthChar);
        this.sentenceCount = new SimpleIntegerProperty(sentenceCount);
        this.averageSentenceLengthCharacters = new SimpleDoubleProperty(averageSentenceLengthCharacters);
        this.averageSentenceLengthCharactersWithoutWhitespaces = new SimpleDoubleProperty(averageSentenceLengthCharactersWithoutWhitespaces);
        this.averageSentenceLengthWords = new SimpleDoubleProperty(averageSentenceLengthWords);
        this.posTagCount = new SimpleMapProperty<>(posTagCount);
    }
    public TextItemData() {
        this.text = new SimpleStringProperty("");
        this.textAndPosTags = new SimpleStringProperty("");
        this.posTagsPerSentence = new SimpleStringProperty("");
        this.wordCount = new SimpleIntegerProperty(0);
        this.averageWordLengthChar = new SimpleDoubleProperty(0.0);
        this.sentenceCount = new SimpleIntegerProperty(0);
        this.averageSentenceLengthCharacters = new SimpleDoubleProperty(0.0);
        this.averageSentenceLengthCharactersWithoutWhitespaces = new SimpleDoubleProperty(0.0);
        this.posTagCount = new SimpleMapProperty<>();
    }

    public double getAverageSentenceLengthWords() {
        return averageSentenceLengthWords.get();
    }

    public void setAverageSentenceLengthWords(double averageSentenceLengthWords) {
        this.averageSentenceLengthWords.set(averageSentenceLengthWords);
    }

    public DoubleProperty averageSentenceLengthWordsProperty() {
        return averageSentenceLengthWords;
    }

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public StringProperty textProperty() {
        return text;
    }

    public String getTextAndPosTags() {
        return textAndPosTags.get();
    }

    public void setTextAndPosTags(String textAndPosTags) {
        this.textAndPosTags.set(textAndPosTags);
    }

    public StringProperty textAndPosTagsProperty() {
        return textAndPosTags;
    }

    public String getPosTagsPerSentence() {
        return posTagsPerSentence.get();
    }

    public void setPosTagsPerSentence(String posTagsPerSentence) {
        this.posTagsPerSentence.set(posTagsPerSentence);
    }

    public StringProperty posTagsPerSentenceProperty() {
        return posTagsPerSentence;
    }

    public int getWordCount() {
        return wordCount.get();
    }

    public void setWordCount(int wordCount) {
        this.wordCount.set(wordCount);
    }

    public IntegerProperty wordCountProperty() {
        return wordCount;
    }

    public double getAverageWordLengthChar() {
        return averageWordLengthChar.get();
    }

    public void setAverageWordLengthChar(double averageWordLengthChar) {
        this.averageWordLengthChar.set(averageWordLengthChar);
    }

    public DoubleProperty averageWordLengthCharProperty() {
        return averageWordLengthChar;
    }

    public int getSentenceCount() {
        return sentenceCount.get();
    }

    public void setSentenceCount(int sentenceCount) {
        this.sentenceCount.set(sentenceCount);
    }

    public IntegerProperty sentenceCountProperty() {
        return sentenceCount;
    }

    public double getAverageSentenceLengthCharacters() {
        return averageSentenceLengthCharacters.get();
    }

    public void setAverageSentenceLengthCharacters(double averageSentenceLengthCharacters) {
        this.averageSentenceLengthCharacters.set(averageSentenceLengthCharacters);
    }

    public DoubleProperty averageSentenceLengthCharactersProperty() {
        return averageSentenceLengthCharacters;
    }

    public double getAverageSentenceLengthCharactersWithoutWhitespaces() {
        return averageSentenceLengthCharactersWithoutWhitespaces.get();
    }

    public void setAverageSentenceLengthCharactersWithoutWhitespaces(double averageSentenceLengthCharactersWithoutWhitespaces) {
        this.averageSentenceLengthCharactersWithoutWhitespaces.set(averageSentenceLengthCharactersWithoutWhitespaces);
    }

    public DoubleProperty averageSentenceLengthCharactersWithoutWhitespacesProperty() {
        return averageSentenceLengthCharactersWithoutWhitespaces;
    }

    public ObservableMap<String, Integer> getPosTagCount() {
        return posTagCount.get();
    }

    public void setPosTagCount(ObservableMap<String, Integer> posTagCount) {
        this.posTagCount.set(posTagCount);
    }

    public MapProperty<String, Integer> posTagCountProperty() {
        return posTagCount;
    }
}
