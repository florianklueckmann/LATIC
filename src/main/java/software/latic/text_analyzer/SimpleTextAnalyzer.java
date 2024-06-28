package software.latic.text_analyzer;

import software.latic.connectives.BaseConnectives;
import software.latic.frequency.FrequencyCalculator;
import software.latic.helper.TagMapper;
import software.latic.item.TextItemData;
import software.latic.syllables.SyllableProvider;
import software.latic.task.TaskLevel;
import software.latic.translation.Translation;
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
                            || (task.getLevel().equals(TaskLevel.TEXT_READABILITY) &! task.getId().toLowerCase().endsWith("level"))) {
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
    
    public int wordsWithMoreThanTwoSyllablesCount() {
        return SyllableProvider.getInstance().getWordsWithMoreThanTwoSyllables();
    }

    public int wordsWithMoreThanThreeSyllables() {
        return SyllableProvider.getInstance().getWordsWithMoreThanThreeSyllables();
    }

    public double wordsWithMoreThanTwoSyllablesPercent() {
        return ((double) wordsWithMoreThanTwoSyllablesCount() / wordCount() * 100);
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

    public String lixReadabilityLevel() {
        String difficulty = "";

        if (lixReadabilityScore() >= 0.0 && lixReadabilityScore() < 29.5) {
            difficulty = "veryEasy";
        } else if (lixReadabilityScore() >= 29.5 && lixReadabilityScore() < 39.5) {
            difficulty = "easy";
        } else if (lixReadabilityScore() >= 39.5 && lixReadabilityScore() < 49.5) {
            difficulty = "medium";
        } else if (lixReadabilityScore() >= 49.5 && lixReadabilityScore() < 59.5) {
            difficulty = "difficult";
        } else if (lixReadabilityScore() >= 59.5) {
            difficulty = "veryDifficult";
        }

        return Translation.getInstance().getTranslation(difficulty);

    }

    //German indices
    public double fleschIndexGerman() {
        return 180 - averageSentenceLengthWords() - (58.5 * averageWordLengthSyllables());
    }

    private String determineFleschIndexLevel(double fleschIndex) {
        String translationKey;
        if (fleschIndex >= 0.0 && fleschIndex < 29.5) {
            translationKey = "veryDifficult";
        } else if (fleschIndex >= 29.5 && fleschIndex < 49.5) {
            translationKey = "difficult";
        } else if (fleschIndex >= 49.5 && fleschIndex < 59.5) {
            translationKey = "moderatelyDifficult";
        } else if (fleschIndex >= 59.5 && fleschIndex < 69.5) {
            translationKey = "medium";
        } else if (fleschIndex >= 69.5 && fleschIndex < 79.5) {
            translationKey = "moderatelyEasy";
        } else if (fleschIndex >= 79.5 && fleschIndex < 89.5) {
            translationKey = "easy";
        } else {
            translationKey = "veryEasy";
        }

        return Translation.getInstance().getTranslation(translationKey);
    }

    public String fleschIndexGermanLevel() {
        return determineFleschIndexLevel(fleschIndexGerman());
    }

    public double wienerSachtextformel() {
        return 0.2656 * averageSentenceLengthWords() + 0.2744 * wordsWithMoreThanTwoSyllablesPercent() - 1.693;
    }

    private String determineWienerSachtextFormelGSMOG(double value) {
        String grade = "";

        if (value < 3.5) {
            grade = "1, 2, 3";
        } else if (value < 4.4) {
            grade = "4";
        } else if (value < 5.4) {
            grade = "5";
        } else if (value < 6.4) {
            grade = "6";
        } else if (value < 7.4) {
            grade = "7";
        } else if (value < 8.4) {
            grade = "8";
        } else if (value < 9.4) {
            grade = "9";
        } else if (value < 10.4) {
            grade = "10";
        } else if (value < 11.4) {
            grade = "11";
        } else if (value < 12.4) {
            grade = "12";
        } else if (value < 13.4) {
            grade = "13";
        } else {
            grade = "> 13";
        }
        return Translation.getInstance().getTranslation("suitableForGrades") + " " + grade;
    }

    public String wienerSachtextformelLevel() {
       return determineWienerSachtextFormelGSMOG(wienerSachtextformel());
    }

    public double gSMOG() {return Math.sqrt( (double) (wordsWithMoreThanTwoSyllablesCount() * 30) / sentenceCount()) - 2;}

    public String gSMOGLevel() {
        return determineWienerSachtextFormelGSMOG(gSMOG());
    }

    //English indices
    public double fleschIndexEnglish() {
        return 206.835 - (1.015 * averageSentenceLengthWords()) - (84.6 * averageWordLengthSyllables());
    }

    public String fleschIndexEnglishLevel() {
        return determineFleschIndexLevel(fleschIndexEnglish());
    }

    public double fleschKincaid() {
        return ((0.39 * wordCount()) / sentenceCount()) + ((11.8 * syllableCount()) / wordCount()) - 15.59;
    }

    public String fleschKincaidLevel() {

        String years = "";

        if (fleschKincaid() < 1.5) {
            years = "1";
        } else if (fleschKincaid() < 2.4) {
            years = "2";
        } else if (fleschKincaid() < 3.4) {
            years = "3";
        } else if (fleschKincaid() < 4.4) {
            years = "4";
        } else if (fleschKincaid() < 5.4) {
            years = "5";
        } else if (fleschKincaid() < 6.4) {
            years = "6";
        } else if (fleschKincaid() < 7.4) {
            years = "7";
        } else if (fleschKincaid() < 8.4) {
            return "8";
        } else if (fleschKincaid() < 9.4) {
            years = "9";
        } else if (fleschKincaid() < 10.4) {
            years = "10";
        } else if (fleschKincaid() < 11.4) {
            years = "11";
        } else if (fleschKincaid() < 12.4) {
            years = "12";
        } else if (fleschKincaid() < 13.4) {
            years = "13";
        } else if (fleschKincaid() < 14.4) {
            years = "14";
        } else if (fleschKincaid() < 15.4) {
            years = "15";
        } else if (fleschKincaid() < 16.4) {
            years = "16";
        } else if (fleschKincaid() < 17.4) {
            years = "17";
        } else {
            years = "More than 17";

        }
        return years + " " + Translation.getInstance().getTranslation("yearsOfSchoolingNeeded");
    }

    private String determineGunningFogAndAriLevel(double value) {
        String grade = "";
        if (value < 1.5) {
            grade = "1";
        } else if (value < 2.4) {
            grade = "2";
        } else if (value < 3.4) {
            grade = "3";
        } else if (value < 4.4) {
            grade = "4";
        } else if (value < 5.4) {
            grade = "5";
        } else if (value < 6.4) {
            grade = "6";
        } else if (value < 7.4) {
            grade = "7";
        } else if (value < 8.4) {
            grade = "8";
        } else if (value < 9.4) {
            grade = "9";
        } else if (value < 10.4) {
            grade = "10";
        } else if (value < 11.4) {
            grade = "11";
        } else if (value < 12.4) {
            grade = "12";
        } else {
            grade = "13";
        }
        return Translation.getInstance().getTranslation("suitableForGrade") + " " + grade;
    }

    public double gunningFog() {
        return 0.4 * wordCount() / sentenceCount() + (double) wordsWithMoreThanTwoSyllablesCount() / wordCount();
    }

    public String gunningFogLevel() {
        return determineGunningFogAndAriLevel(gunningFog());
    }

    public double automatedReadabilityIndex () {
        return 4.71 * textCountCharactersWithoutPunctuation() /  wordCount() + 0.5 *wordCount() / sentenceCount() - 21.43;
    }

    public String automatedReadabilityIndexLevel() {
        return determineGunningFogAndAriLevel(automatedReadabilityIndex());
    }

    public double colemanLiau () { return 0.0588 * ( (double) textCountCharactersWithoutPunctuation() / (double) wordCount() * 100 ) - 0.296 * ( (double)sentenceCount() / (double) wordCount() * 100 ) - 15.8; }

    public String colemanLiauLevel() {

        String grade = "";

        if (colemanLiau() < 6.4) {
            grade = "6";
        } else if (colemanLiau() < 7.4) {
            grade = "7";
        } else if (colemanLiau() < 8.4) {
            grade = "8";
        } else if (colemanLiau() < 9.4) {
            grade = "9";
        } else if (colemanLiau() < 10.4) {
            grade = "10";
        } else if (colemanLiau() < 11.4) {
            grade = "11";
        } else if (colemanLiau() < 12.4) {
            grade = "12";
        } else if (colemanLiau() < 13.4) {
            grade = Translation.getInstance().getTranslation("college") + "(" + Translation.getInstance().getTranslation("firstYear") + ")";
        } else if (colemanLiau() < 14.4) {
            grade = Translation.getInstance().getTranslation("college") + "(" + Translation.getInstance().getTranslation("sophomore") + ")";
        } else if (colemanLiau() < 15.4) {
            grade = Translation.getInstance().getTranslation("college") + "(" + Translation.getInstance().getTranslation("junior") + ")";
        } else if (colemanLiau() < 16.4) {
            grade = Translation.getInstance().getTranslation("college") + "(" + Translation.getInstance().getTranslation("senior") + ")";
        } else {
            grade = Translation.getInstance().getTranslation("college") + "(" + Translation.getInstance().getTranslation("graduate") + ")";
        }

        return Translation.getInstance().getTranslation("suitableForGrade") + " " + grade;
    }

    public double SMOG () { return 1.043 * Math.sqrt(30 *  (double) wordsWithMoreThanTwoSyllablesCount() / (double) sentenceCount() + 3.1291); }

    public String SMOGLevel() {
        return determineGunningFogAndAriLevel(SMOG());
    }

    public int connectivesCount () {
        return BaseConnectives.getInstance().connectivesInDocument(doc);
    }

    public double averageWordFrequencyClass() {
        return FrequencyCalculator.getInstance().calculateAverageWordFrequencyClass(doc.sentences().stream().flatMap(sentence -> sentence.words().stream()).toList());
    }
}

