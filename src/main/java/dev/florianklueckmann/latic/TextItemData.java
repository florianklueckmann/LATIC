package dev.florianklueckmann.latic;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public abstract class TextItemData {
    final StringProperty text;
    StringProperty textAndPosTags;
    StringProperty posTagsPerSentence;
    IntegerProperty wordCount;
    DoubleProperty averageWordLengthCharacters;
    IntegerProperty sentenceCount;
    DoubleProperty averageSentenceLengthCharacters;
    DoubleProperty averageSentenceLengthCharactersWithoutWhitespaces;
    DoubleProperty averageSentenceLengthWords;
    DoubleProperty lexicalDiversity;
    DoubleProperty lixReadabilityScore;

//    IntegerProperty advers;
//    IntegerProperty determiner;
//    IntegerProperty interjections;
//    IntegerProperty modals;
//    IntegerProperty nouns;
//    IntegerProperty numbers;
//    IntegerProperty particles;
//    IntegerProperty pronouns;
//    IntegerProperty properNouns;
//    IntegerProperty symbols;
//    IntegerProperty verbs;
//    IntegerProperty punctuation;
//    IntegerProperty unknown;


    public double getLexicalDiversity()
    {
        return lexicalDiversity.get();
    }

    public DoubleProperty lexicalDiversityProperty()
    {
        return lexicalDiversity;
    }

    public void setLexicalDiversity(double lexicalDiversity)
    {
        this.lexicalDiversity.set(lexicalDiversity);
    }

    public double getLixReadabilityScore()
    {
        return lixReadabilityScore.get();
    }

    public DoubleProperty lixReadabilityScoreProperty()
    {
        return lixReadabilityScore;
    }

    public void setLixReadabilityScore(double lixReadabilityScore)
    {
        this.lixReadabilityScore.set(lixReadabilityScore);
    }

    ListProperty<IntegerLinguisticFeature> posTagCount;

    private HashMap<String, LinguisticFeature> properties;

    //Set properties
    public Object setProperty(String key, LinguisticFeature value) {
        return this.properties.put(key, value); //Returns old value if existing
    }

    //Get properties
    public LinguisticFeature getProperty(String key) {
        return this.properties.getOrDefault(key, null);
    }

    public HashMap<String, LinguisticFeature> getProperties()
    {
        return properties;
    }

    public void setProperties(HashMap<String, LinguisticFeature> properties)
    {
        this.properties = properties;
    }


    public TextItemData(String text) {
        this.text = new SimpleStringProperty(text);
        this.textAndPosTags = new SimpleStringProperty();
        this.posTagsPerSentence = new SimpleStringProperty();
        this.wordCount = new SimpleIntegerProperty();
        this.averageWordLengthCharacters = new RoundedDoubleProperty();
        this.sentenceCount = new SimpleIntegerProperty();
        this.averageSentenceLengthCharacters = new RoundedDoubleProperty();
        this.averageSentenceLengthCharactersWithoutWhitespaces = new RoundedDoubleProperty();
        this.averageSentenceLengthWords = new RoundedDoubleProperty();
        this.lexicalDiversity = new RoundedDoubleProperty();
        this.lixReadabilityScore = new RoundedDoubleProperty();
    }

    public String[] getValues() {
        return new String[] {
                getText(),
                getTextAndPosTags(),
                getPosTagsPerSentence(),
                String.valueOf(getWordCount()),
                String.valueOf(getAverageWordLengthCharacters()),
                String.valueOf(getSentenceCount()),
                String.valueOf(getAverageSentenceLengthCharacters()),
                String.valueOf(getAverageSentenceLengthCharactersWithoutWhitespaces()),
                String.valueOf(getLexicalDiversity()),
                String.valueOf(getLexicalDiversity())
        };
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
        System.out.println("text: " + text.get());
        if(!text.get().isEmpty())
            return text.get();
        else return "Empty";
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

    public double getAverageWordLengthCharacters() {
        return averageWordLengthCharacters.get();
    }

    public void setAverageWordLengthCharacters(double averageWordLengthCharacters) {
        this.averageWordLengthCharacters.set(averageWordLengthCharacters);
    }

    public DoubleProperty averageWordLengthCharactersProperty() {
        return averageWordLengthCharacters;
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
}
