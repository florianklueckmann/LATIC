package dev.florianklueckmann.latic;

import dev.florianklueckmann.latic.services.DocumentKeeper;
import dev.florianklueckmann.latic.services.NlpTextAnalyzer;
import dev.florianklueckmann.latic.services.SimpleTextAnalyzer;
import dev.florianklueckmann.latic.services.TextFormattingService;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextFormatter;
import org.apache.log4j.BasicConfigurator;

import javax.annotation.RegEx;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
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
        }
    }

    public void printSentences() {
        for (var sent : doc.sentences()) {
            System.out.println(" ");
            System.out.println(sent);
            System.out.println(" ");
        }
    }

    public void parseSentences() {
        for (Sentence sent : doc.sentences()) {
            //System.out.println("The parse of the sentence '" + sent + "' is " + sent.parse()); //11.4sec
            System.out.println("lemmas" + sent.lemmas());
            System.out.println("tokenize" + sent.words());
        }
    }


    protected int getAverageSentenceLengthSyllables() {
        return 0;
    }

    //CORENLP Stuff
    void testingStuff() {

        //var sent = doc.sentence(0);

        for (var sen : doc.sentences()) {
            var posTags = sen.posTags();
            log(sen);
            log("POS: \n" + posTags);
            log((int) posTags.stream().filter(e -> e.equals("ADP")).count());
        }

        System.out.println("WC: " +simpleTextAnalyzer.getWordCount());
        System.out.println("SC: " +simpleTextAnalyzer.getSentenceCount());
        System.out.println("AVGSENTWW: " +simpleTextAnalyzer.getAverageSentenceLengthCharactersWithoutWhitespaces());
        System.out.println("AVGSENT: " +simpleTextAnalyzer.getAverageSentenceLengthCharacters());
        System.out.println();
    }


    protected void analyzeAndPrintToConsole() {
        System.out.println("Hi");

        setLanguage("german");

        simpleTextAnalyzer.setDoc(doc);
        System.out.println(simpleTextAnalyzer.getTextLength(paragraphs));
        //initializeDocument(input);
        System.out.println(doc.text());

        testingStuff();
    }

    private void log(Object o) {
        System.out.println(o);
    }
}
