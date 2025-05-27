package software.latic.item;

import javafx.collections.ObservableList;
import software.latic.linguistic_feature.IntegerLinguisticFeature;
import software.latic.linguistic_feature.LinguisticFeature;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;

public abstract class TextItemData {
    final StringProperty fileName;
    final StringProperty text;
    StringProperty textAndPosTags;
    IntegerProperty wordCount;
    IntegerProperty sentenceCount;
    IntegerProperty syllableCount;
    IntegerProperty wordsWithMoreThanTwoSyllablesCount;
    DoubleProperty averageWordLengthCharacters;
    DoubleProperty averageWordLengthSyllables;
    DoubleProperty averageSentenceLengthCharacters;
    DoubleProperty averageSentenceLengthCharactersWithoutWhitespaces;
    DoubleProperty averageSentenceLengthWords;
    DoubleProperty typeTokenRatio;
    DoubleProperty rootedTypeTokenRatio;
    DoubleProperty lixReadabilityScore;
    StringProperty lixReadabilityLevel;
    DoubleProperty averageSentenceLengthSyllables;

    IntegerProperty connectivesCount;

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


    public double getTypeTokenRatio()
    {
        return typeTokenRatio.get();
    }

    public DoubleProperty typeTokenRatioProperty()
    {
        return typeTokenRatio;
    }

    public void setTypeTokenRatio(double typeTokenRatio)
    {
        this.typeTokenRatio.set(typeTokenRatio);
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
        this.fileName = new SimpleStringProperty();
        this.text = new SimpleStringProperty(text);
        this.textAndPosTags = new SimpleStringProperty();
        this.wordCount = new SimpleIntegerProperty();
        this.sentenceCount = new SimpleIntegerProperty();
        this.syllableCount = new SimpleIntegerProperty();
        this.wordsWithMoreThanTwoSyllablesCount = new SimpleIntegerProperty();
        this.averageWordLengthCharacters = new RoundedDoubleProperty();
        this.averageWordLengthSyllables = new RoundedDoubleProperty();
        this.averageSentenceLengthCharacters = new RoundedDoubleProperty();
        this.averageSentenceLengthCharactersWithoutWhitespaces = new RoundedDoubleProperty();
        this.averageSentenceLengthWords = new RoundedDoubleProperty();
        this.averageSentenceLengthSyllables = new RoundedDoubleProperty();
        this.typeTokenRatio = new RoundedDoubleProperty();
        this.rootedTypeTokenRatio = new RoundedDoubleProperty();
        this.lixReadabilityScore = new RoundedDoubleProperty();
        this.lixReadabilityLevel = new SimpleStringProperty();
        this.connectivesCount = new SimpleIntegerProperty();
    }

    public ObservableMap<String, String> getIdValueMap() {
        var valueMap = new HashMap<String, String>();
        valueMap.put("fileName", getFileName());
        valueMap.put("text",getText());
        valueMap.put("textAndPosTags",getTextAndPosTags());
        valueMap.put("wordCount",String.valueOf(getWordCount()));
        valueMap.put("sentenceCount",String.valueOf(getSentenceCount()));
        valueMap.put("syllableCount",String.valueOf(getSyllableCount()));
        valueMap.put("wordsWithMoreThanTwoSyllablesCount",String.valueOf(getWordsWithMoreThanTwoSyllablesCount()));
        valueMap.put("averageWordLengthCharacters",String.valueOf(getAverageWordLengthCharacters()));
        valueMap.put("averageWordLengthSyllables",String.valueOf(getAverageWordLengthSyllables()));
        valueMap.put("averageSentenceLengthCharacters",String.valueOf(getAverageSentenceLengthCharacters()));
        valueMap.put("averageSentenceLengthCharactersWithoutWhitespaces",String.valueOf(getAverageSentenceLengthCharactersWithoutWhitespaces()));
        valueMap.put("averageSentenceLengthWords",String.valueOf(getAverageSentenceLengthWords()));
        valueMap.put("averageSentenceLengthSyllables",String.valueOf(getAverageSentenceLengthWords()));
        valueMap.put("typeTokenRatio",String.valueOf(getTypeTokenRatio()));
        valueMap.put("rootedTypeTokenRatio",String.valueOf(getRootedTypeTokenRatio()));
        valueMap.put("lixReadabilityScore",String.valueOf(getLixReadabilityScore()));
        valueMap.put("lixReadabilityLevel",String.valueOf(getLixReadabilityLevel()));
        valueMap.put("connectivesCount",String.valueOf(getConnectivesCount()));

        return FXCollections.observableMap(valueMap);
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

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public ObservableList<IntegerLinguisticFeature> getPosTagCount() {
        return posTagCount.get();
    }

    public ListProperty<IntegerLinguisticFeature> posTagCountProperty() {
        return posTagCount;
    }

    public void setPosTagCount(ObservableList<IntegerLinguisticFeature> posTagCount) {
        this.posTagCount.set(posTagCount);
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

    public double getAverageSentenceLengthSyllables() {
        return averageSentenceLengthSyllables.get();
    }

    public DoubleProperty averageSentenceLengthSyllablesProperty() {
        return averageSentenceLengthSyllables;
    }

    public void setAverageSentenceLengthSyllables(double averageSentenceLengthSyllables) {
        this.averageSentenceLengthSyllables.set(averageSentenceLengthSyllables);
    }

    public double getAverageWordLengthSyllables() {
        return averageWordLengthSyllables.get();
    }

    public DoubleProperty averageWordLengthSyllablesProperty() {
        return averageWordLengthSyllables;
    }

    public void setAverageWordLengthSyllables(double averageWordLengthSyllables) {
        this.averageWordLengthSyllables.set(averageWordLengthSyllables);
    }

    public int getSyllableCount() {
        return syllableCount.get();
    }

    public IntegerProperty syllableCountProperty() {
        return syllableCount;
    }

    public void setSyllableCount(int syllableCount) {
        this.syllableCount.set(syllableCount);
    }

    public int getConnectivesCount() {return connectivesCount.get();}

    public IntegerProperty connectivesCountProperty() {return connectivesCount;}

    public void setConnectivesCount(int connectivesCount) {this.connectivesCount.set(connectivesCount);}

    public int getWordsWithMoreThanTwoSyllablesCount() {
        return wordsWithMoreThanTwoSyllablesCount.get();
    }

    public IntegerProperty wordsWithMoreThanTwoSyllablesCountProperty() {
        return wordsWithMoreThanTwoSyllablesCount;
    }

    public void setWordsWithMoreThanTwoSyllablesCount(int wordsWithMoreThanTwoSyllables) {
        this.wordsWithMoreThanTwoSyllablesCount.set(wordsWithMoreThanTwoSyllables);
    }

    public String getLixReadabilityLevel() {
        return lixReadabilityLevel.get();
    }

    public StringProperty lixReadabilityLevelProperty() {
        return lixReadabilityLevel;
    }

    public void setLixReadabilityLevel(String lixReadabilityLevel) {
        this.lixReadabilityLevel.set(lixReadabilityLevel);
    }

    public double getRootedTypeTokenRatio() {
        return rootedTypeTokenRatio.get();
    }

    public DoubleProperty rootedTypeTokenRatioProperty() {
        return rootedTypeTokenRatio;
    }

    public void setRootedTypeTokenRatio(double rootedTypeTokenRatio) {
        this.rootedTypeTokenRatio.set(rootedTypeTokenRatio);
    }
}
