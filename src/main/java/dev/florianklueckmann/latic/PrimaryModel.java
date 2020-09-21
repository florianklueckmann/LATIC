package dev.florianklueckmann.latic;

import dev.florianklueckmann.latic.services.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class PrimaryModel {

    private DocumentKeeper documentKeeper;
    private String language = "english"; //TODO: Maybe use ENUM
    private Properties props;
    SimpleTextAnalyzer simpleTextAnalyzer;
    TextFormattingService textFormattingService;
    NlpTextAnalyzer nlp;

    public List<CharSequence> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<CharSequence> paragraphs) {
        this.paragraphs = paragraphs;
    }

    List<CharSequence> paragraphs;

    public Document getDoc() {
        return doc;
    }

    Document doc;


    public PrimaryModel(SimpleTextAnalyzer simpleTextAnalyzer, TextFormattingService textFormattingService, NlpTextAnalyzer nlp) {
        BasicConfigurator.configure();
        props = new Properties();

        this.simpleTextAnalyzer = simpleTextAnalyzer;
        this.textFormattingService = textFormattingService;
        this.nlp = nlp;
    }

    //TODO: Throw Exception if paragraphs not set?
    public void initializeDocument() {
        this.doc = new Document(props, this.paragraphs.stream()
                .map(charSequence -> charSequence.toString().trim())
                .collect(Collectors.joining(" ")));
    }

    public void initializeDocument(String text) {
        this.doc = new Document(props, text);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        //TODO: Lookup Option<String>
        if (language.length() > 0) {
            this.language = language;
            if (!language.equals("english")) {
                try {
                    props.load(IOUtils.readerFromString("StanfordCoreNLP-" + language + ".properties"));
                } catch (IOException e) {
                    //TODO: Error Handling
                    e.printStackTrace();
                }
            }
            //TODO Find a better way to set language
            if (language.equals("english")) {
                props = new Properties();
            }
        }
    }

    public void printSentences() {
        for (var sent : doc.sentences()) {
            System.out.println(" ");
            System.out.println(sent);
            System.out.println(" ");
        }
    }

    public String wordClassesAsString() {
        var sb = new StringBuilder();

        WordClassService wordClassCounter;

        if (language.toLowerCase().equals("german"))
            wordClassCounter = new GermanWordClassService();
        else
            wordClassCounter = new EnglishWordClassService();

        wordClassCounter.analyzeWordClasses(doc.sentences()).entrySet().stream()
                .filter(stringIntegerEntry -> stringIntegerEntry.getValue() > 0)
                .forEach(stringIntegerEntry ->
                        sb.append(stringIntegerEntry.getKey()).append(": ").append(stringIntegerEntry.getValue()).append("\n"));

        return sb.toString();
    }

    public List<IntegerLinguisticFeature> wordClassesAsList() {

        List<IntegerLinguisticFeature> featureList = new ArrayList<>();

        WordClassService wordClassCounter;

        if (language.toLowerCase().equals("german"))
            wordClassCounter = new GermanWordClassService();
        else
            wordClassCounter = new EnglishWordClassService();

        wordClassCounter.analyzeWordClasses(doc.sentences())
                .forEach((key, value) -> featureList.add(new IntegerLinguisticFeature(key, key, value)));

        return featureList;
    }

    protected int getAverageSentenceLengthSyllables() {
        return 0;
    }

    protected String analyze() {
        // setLanguage("german");

        simpleTextAnalyzer.setDoc(doc);

        var sb = new StringBuilder()
                .append(appendResultLine("Word count", simpleTextAnalyzer.wordCount()))
                .append(appendResultLine("Sentence count", simpleTextAnalyzer.sentenceCount()))
                .append(appendResultLine("Average sentence length without whitespaces", simpleTextAnalyzer.averageSentenceLengthCharactersWithoutWhitespaces()))
                .append(appendResultLine("Average sentence length", simpleTextAnalyzer.averageSentenceLengthCharacters()))
                .append(wordClassesAsString());

        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(sentence.posTags()).append("\n"));

        return sb.toString();

    }

    protected List<String> sentencesAndPosTags() {
        List<String> results = new ArrayList<>();
        doc.sentences().forEach(sentence -> results.add(sentence + "\n" + sentence.posTags()));
        return results;
    }

    protected List<LinguisticFeature> analyzeGeneralItemCharacteristics() {
        simpleTextAnalyzer.setDoc(doc);
        nlp.setDoc(doc);

        ObservableList<LinguisticFeature> featureList = FXCollections.observableArrayList();
        List<String> errorList = new ArrayList<>();

        for (var lingFeature : GeneralItemCharacteristics.values()) {
            java.lang.reflect.Method method;
            try {
                method = simpleTextAnalyzer.getClass().getMethod(lingFeature.getId());
                if (lingFeature.getId().contains("average"))
                    featureList.add(new DoubleLinguisticFeature(
                            lingFeature.getName(),
                            lingFeature.getId(),
                            (double) method.invoke(simpleTextAnalyzer)));
                else if (lingFeature.getId().contains("count"))
                    featureList.add(new IntegerLinguisticFeature(
                            lingFeature.getName(),
                            lingFeature.getId(),
                            (int) method.invoke(simpleTextAnalyzer)));
                else
                    featureList.add(new StringLinguisticFeature(
                            lingFeature.getName(),
                            lingFeature.getId(),
                            String.valueOf(method.invoke(simpleTextAnalyzer))));

            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
                featureList.add(new StringLinguisticFeature(lingFeature.getName(), lingFeature.getId(), "NoSuchMethodException"));
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
                featureList.add(new StringLinguisticFeature(lingFeature.getName(), lingFeature.getId(), "IllegalAccessException"));
            } catch (InvocationTargetException e) {
//                e.printStackTrace();
                featureList.add(new StringLinguisticFeature(lingFeature.getName(), lingFeature.getId(), "InvocationTargetException"));
            }
        }

        return featureList;
    }

    protected String appendResultLine(String label, double result) {
        return label + ": " + result + "\n";
    }

    protected String appendResultLine(String label, int result) {
        return label + ": " + result + "\n";
    }

    protected String appendResultLine(String label, String result) {
        return label + ": " + result + "\n";
    }

    private void log(Object o) {
        System.out.println(o);
    }
}
