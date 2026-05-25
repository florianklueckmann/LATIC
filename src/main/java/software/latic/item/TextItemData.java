package software.latic.item;

import javafx.collections.ObservableList;
import software.latic.linguistic_feature.IntegerLinguisticFeature;
import software.latic.linguistic_feature.LinguisticFeature;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class TextItemData {
    final StringProperty fileName;
    final StringProperty text;
    StringProperty textAndPosTags;
    IntegerProperty passiveConstructionsCount;
    IntegerProperty nounPhrasesCount;
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
    DoubleProperty rootTypeTokenRatio;
    DoubleProperty lixReadabilityScore;
    StringProperty lixReadabilityLevel;
    DoubleProperty averageSentenceLengthSyllables;

    IntegerProperty pagesCount;
    DoubleProperty fontSizeMm;
    IntegerProperty subordinateClausesCount;
    DoubleProperty lixPlusScore;
    DoubleProperty brelix0Score;
    DoubleProperty brelix1Score;
    DoubleProperty brelix2Score;
    DoubleProperty brelix3Score;
    DoubleProperty brelix3NeuScore;
    DoubleProperty brelix4Score;
    DoubleProperty brelix5Score;

    IntegerProperty lixPlusLevel;
    IntegerProperty brelix0Level;
    IntegerProperty brelix1Level;
    IntegerProperty brelix2Level;
    IntegerProperty brelix3Level;
    IntegerProperty brelix3NeuLevel;
    IntegerProperty brelix4Level;
    IntegerProperty brelix5Level;

    StringProperty brelixDebugInfo;
    private final LinkedHashMap<String, String> brelixDebugMap = new LinkedHashMap<>();

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
        this.passiveConstructionsCount = new SimpleIntegerProperty();
        this.nounPhrasesCount = new SimpleIntegerProperty();
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
        this.pagesCount = new SimpleIntegerProperty(1);
        this.fontSizeMm = new RoundedDoubleProperty(6.0);
        this.subordinateClausesCount = new SimpleIntegerProperty();
        this.lixPlusScore = new RoundedDoubleProperty();
        this.brelix0Score = new RoundedDoubleProperty();
        this.brelix1Score = new RoundedDoubleProperty();
        this.brelix2Score = new RoundedDoubleProperty();
        this.brelix3Score = new RoundedDoubleProperty();
        this.brelix3NeuScore = new RoundedDoubleProperty();
        this.brelix4Score = new RoundedDoubleProperty();
        this.brelix5Score = new RoundedDoubleProperty();
        this.lixPlusLevel = new SimpleIntegerProperty();
        this.brelix0Level = new SimpleIntegerProperty();
        this.brelix1Level = new SimpleIntegerProperty();
        this.brelix2Level = new SimpleIntegerProperty();
        this.brelix3Level = new SimpleIntegerProperty();
        this.brelix3NeuLevel = new SimpleIntegerProperty();
        this.brelix4Level = new SimpleIntegerProperty();
        this.brelix5Level = new SimpleIntegerProperty();
        this.brelixDebugInfo = new SimpleStringProperty();
        this.typeTokenRatio = new RoundedDoubleProperty();
        this.rootTypeTokenRatio = new RoundedDoubleProperty();
        this.lixReadabilityScore = new RoundedDoubleProperty();
        this.lixReadabilityLevel = new SimpleStringProperty();
        this.connectivesCount = new SimpleIntegerProperty();
    }

    public ObservableMap<String, String> getIdValueMap() {
        var valueMap = new HashMap<String, String>();
        valueMap.put("fileName", getFileName());
        valueMap.put("text",getText());
        valueMap.put("textAndPosTags",getTextAndPosTags());
        valueMap.put("passiveConstructionsCount", String.valueOf(getPassiveConstructionsCount()));
        valueMap.put("nounPhrasesCount", String.valueOf(getPassiveConstructionsCount()));
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
        valueMap.put("rootTypeTokenRatio",String.valueOf(getRootTypeTokenRatio()));
        valueMap.put("lixReadabilityScore",String.valueOf(getLixReadabilityScore()));
        valueMap.put("lixReadabilityLevel",String.valueOf(getLixReadabilityLevel()));
        valueMap.put("pagesCount", String.valueOf(getPagesCount()));
        valueMap.put("fontSizeMm", String.valueOf(getFontSizeMm()));
        valueMap.put("subordinateClausesCount", String.valueOf(getSubordinateClausesCount()));
        valueMap.put("lixPlusScore", String.valueOf(getLixPlusScore()));
        valueMap.put("lixPlusLevel", String.valueOf(getLixPlusLevel()));
        valueMap.put("brelix0Score", String.valueOf(getBrelix0Score()));
        valueMap.put("brelix0Level", String.valueOf(getBrelix0Level()));
        valueMap.put("brelix1Score", String.valueOf(getBrelix1Score()));
        valueMap.put("brelix1Level", String.valueOf(getBrelix1Level()));
        valueMap.put("brelix2Score", String.valueOf(getBrelix2Score()));
        valueMap.put("brelix2Level", String.valueOf(getBrelix2Level()));
        valueMap.put("brelix3Score", String.valueOf(getBrelix3Score()));
        valueMap.put("brelix3Level", String.valueOf(getBrelix3Level()));
        valueMap.put("brelix3NeuScore", String.valueOf(getBrelix3NeuScore()));
        valueMap.put("brelix3NeuLevel", String.valueOf(getBrelix3NeuLevel()));
        valueMap.put("brelix4Score", String.valueOf(getBrelix4Score()));
        valueMap.put("brelix4Level", String.valueOf(getBrelix4Level()));
        valueMap.put("brelix5Score", String.valueOf(getBrelix5Score()));
        valueMap.put("brelixDebugInfo", String.valueOf(getBrelixDebugInfo()));
        valueMap.put("brelix5Level", String.valueOf(getBrelix5Level()));
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
        return fileName.isNull().get() ? "" : fileName.get();
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

    public int getPassiveConstructionsCount() {
        return passiveConstructionsCount.get();
    }

    public IntegerProperty passiveConstructionsCountProperty() {
        return passiveConstructionsCount;
    }

    public void setPassiveConstructionsCount(int passiveConstructionsCount) {
        this.passiveConstructionsCount.set(passiveConstructionsCount);
    }

    public int getNounPhrasesCount() {
        return nounPhrasesCount.get();
    }

    public IntegerProperty nounPhrasesCountProperty() {
        return nounPhrasesCount;
    }

    public void setNounPhrasesCount(int nounPhrasesCount) {
        this.nounPhrasesCount.set(nounPhrasesCount);
    }

    public double getRootTypeTokenRatio() {
        return rootTypeTokenRatio.get();
    }

    public DoubleProperty rootTypeTokenRatioProperty() {
        return rootTypeTokenRatio;
    }

    public void setRootTypeTokenRatio(double rootTypeTokenRatio) {
        this.rootTypeTokenRatio.set(rootTypeTokenRatio);
    }

    public int getPagesCount() { return pagesCount.get(); }
    public IntegerProperty pagesCountProperty() { return pagesCount; }
    public void setPagesCount(int pagesCount) { this.pagesCount.set(pagesCount); }

    public double getFontSizeMm() { return fontSizeMm.get(); }
    public DoubleProperty fontSizeMmProperty() { return fontSizeMm; }
    public void setFontSizeMm(double fontSizeMm) { this.fontSizeMm.set(fontSizeMm); }

    public int getSubordinateClausesCount() { return subordinateClausesCount.get(); }
    public IntegerProperty subordinateClausesCountProperty() { return subordinateClausesCount; }
    public void setSubordinateClausesCount(int subordinateClausesCount) { this.subordinateClausesCount.set(subordinateClausesCount); }

    public double getLixPlusScore() { return lixPlusScore.get(); }
    public DoubleProperty lixPlusScoreProperty() { return lixPlusScore; }
    public void setLixPlusScore(double lixPlusScore) { this.lixPlusScore.set(lixPlusScore); }

    public double getBrelix0Score() { return brelix0Score.get(); }
    public DoubleProperty brelix0ScoreProperty() { return brelix0Score; }
    public void setBrelix0Score(double brelix0Score) { this.brelix0Score.set(brelix0Score); }

    public double getBrelix1Score() { return brelix1Score.get(); }
    public DoubleProperty brelix1ScoreProperty() { return brelix1Score; }
    public void setBrelix1Score(double brelix1Score) { this.brelix1Score.set(brelix1Score); }

    public double getBrelix2Score() { return brelix2Score.get(); }
    public DoubleProperty brelix2ScoreProperty() { return brelix2Score; }
    public void setBrelix2Score(double brelix2Score) { this.brelix2Score.set(brelix2Score); }

    public double getBrelix3Score() { return brelix3Score.get(); }
    public DoubleProperty brelix3ScoreProperty() { return brelix3Score; }
    public void setBrelix3Score(double brelix3Score) { this.brelix3Score.set(brelix3Score); }

    public double getBrelix3NeuScore() { return brelix3NeuScore.get(); }
    public DoubleProperty brelix3NeuScoreProperty() { return brelix3NeuScore; }
    public void setBrelix3NeuScore(double brelix3NeuScore) { this.brelix3NeuScore.set(brelix3NeuScore); }

    public double getBrelix4Score() { return brelix4Score.get(); }
    public DoubleProperty brelix4ScoreProperty() { return brelix4Score; }
    public void setBrelix4Score(double brelix4Score) { this.brelix4Score.set(brelix4Score); }

    public double getBrelix5Score() { return brelix5Score.get(); }
    public DoubleProperty brelix5ScoreProperty() { return brelix5Score; }
    public void setBrelix5Score(double brelix5Score) { this.brelix5Score.set(brelix5Score); }

    public int getLixPlusLevel() { return lixPlusLevel.get(); }
    public IntegerProperty lixPlusLevelProperty() { return lixPlusLevel; }
    public void setLixPlusLevel(int lixPlusLevel) { this.lixPlusLevel.set(lixPlusLevel); }

    public int getBrelix0Level() { return brelix0Level.get(); }
    public IntegerProperty brelix0LevelProperty() { return brelix0Level; }
    public void setBrelix0Level(int brelix0Level) { this.brelix0Level.set(brelix0Level); }

    public int getBrelix1Level() { return brelix1Level.get(); }
    public IntegerProperty brelix1LevelProperty() { return brelix1Level; }
    public void setBrelix1Level(int brelix1Level) { this.brelix1Level.set(brelix1Level); }

    public int getBrelix2Level() { return brelix2Level.get(); }
    public IntegerProperty brelix2LevelProperty() { return brelix2Level; }
    public void setBrelix2Level(int brelix2Level) { this.brelix2Level.set(brelix2Level); }

    public int getBrelix3Level() { return brelix3Level.get(); }
    public IntegerProperty brelix3LevelProperty() { return brelix3Level; }
    public void setBrelix3Level(int brelix3Level) { this.brelix3Level.set(brelix3Level); }

    public int getBrelix3NeuLevel() { return brelix3NeuLevel.get(); }
    public IntegerProperty brelix3NeuLevelProperty() { return brelix3NeuLevel; }
    public void setBrelix3NeuLevel(int brelix3NeuLevel) { this.brelix3NeuLevel.set(brelix3NeuLevel); }

    public int getBrelix4Level() { return brelix4Level.get(); }
    public IntegerProperty brelix4LevelProperty() { return brelix4Level; }
    public void setBrelix4Level(int brelix4Level) { this.brelix4Level.set(brelix4Level); }

    public int getBrelix5Level() { return brelix5Level.get(); }
    public IntegerProperty brelix5LevelProperty() { return brelix5Level; }
    public void setBrelix5Level(int brelix5Level) { this.brelix5Level.set(brelix5Level); }

    public String getBrelixDebugInfo() {
        return brelixDebugInfo.get();
    }

    public StringProperty brelixDebugInfoProperty() {
        return brelixDebugInfo;
    }

    public void setBrelixDebugInfo(String brelixDebugInfo) {
        this.brelixDebugInfo.set(brelixDebugInfo);
    }

    public LinkedHashMap<String, String> getBrelixDebugMap() {
        return brelixDebugMap;
    }
}
