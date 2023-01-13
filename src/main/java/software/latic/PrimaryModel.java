package software.latic;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import software.latic.connectives.BaseConnectives;
import software.latic.connectives.ConnectivesProvider;
import software.latic.item.GermanTextItemData;
import software.latic.item.TextItemData;
import software.latic.translation.SupportedLocales;
import software.latic.syllables.SyllableProvider;
import software.latic.translation.Translation;
import software.latic.item.EnglishTextItemData;
import software.latic.linguistic_feature.IntegerLinguisticFeature;
import software.latic.linguistic_feature.LinguisticFeature;
import software.latic.task.Task;
import software.latic.text_analyzer.NlpTextAnalyzer;
import software.latic.text_analyzer.SimpleTextAnalyzer;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import software.latic.word_class_service.EnglishWordClassService;
import software.latic.word_class_service.GermanWordClassService;
import software.latic.word_class_service.TextFormattingService;
import software.latic.word_class_service.WordClassService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class PrimaryModel {

    private Locale language = Translation.getInstance().getLocale(); //TODO: Maybe use ENUM
    private Properties props;
    SimpleTextAnalyzer simpleTextAnalyzer = SimpleTextAnalyzer.getInstance();
    TextFormattingService textFormattingService = TextFormattingService.getInstance();
    NlpTextAnalyzer nlp = NlpTextAnalyzer.getInstance();
    StanfordCoreNLP pipeline;

    public Document getDoc() {
        return doc;
    }

    Document doc;


    public PrimaryModel() {
        props = new Properties();
        this.setLanguage(Translation.getInstance().getLocale());
    }

    public PrimaryModel initializeDocument(List<CharSequence> paragraphs) {
        var text = paragraphs.stream()
                .map(charSequence -> charSequence.toString().trim())
                .collect(Collectors.joining("\n"));

        if (Translation.getInstance().getLocale().equals(SupportedLocales.FRENCH.getLocale())) {
            props.setProperty("annotators", "tokenize, ssplit, pos");
            pipeline = new StanfordCoreNLP(props);
            Annotation annotation = new Annotation(text);
            pipeline.annotate(annotation);
            this.doc = new Document(annotation);
        } else {
            this.doc = new Document(props, text);
        }

        return this;
    }

    public Locale getLanguage() {
        return language;
    }

    private void setLanguage(Locale language) {
            this.language = language;
            if (!language.getLanguage().equalsIgnoreCase("en")) {
                try {
                    props.load(IOUtils.readerFromString("StanfordCoreNLP-" + language.getDisplayLanguage(Locale.ENGLISH).toLowerCase(Locale.ROOT) + ".properties"));
                } catch (IOException e) {
                    //TODO: Error Handling
                    e.printStackTrace();
                }
            }
            //TODO Find a better way to set language
            if (language.getLanguage().equalsIgnoreCase("en")) {
                props = new Properties();
            }
    }

    public void printSentences() {
        for (var sent : doc.sentences()) {
            System.out.println(" ");
            System.out.println(sent);
            System.out.println(" ");
        }
    }

    public ObservableMap<String, IntegerLinguisticFeature> wordClassesAsMap(ObservableList<Task> languageSpecificTasks) {

        ObservableMap<String, IntegerLinguisticFeature> featureMap = FXCollections.observableHashMap();

        WordClassService wordClassCounter;

        //TODO
        if (language.equals(Locale.ENGLISH))
            wordClassCounter = new EnglishWordClassService();
        else
            wordClassCounter = new GermanWordClassService();

        for (var linguisticFeature : wordClassCounter.analyzeWordClasses(doc.sentences())) {
            if (languageSpecificTasks.stream().anyMatch(task -> task.getId().equals(linguisticFeature.getId()) && task.selectedProperty().get()))
                featureMap.put(linguisticFeature.getId(), linguisticFeature);
        }

        return featureMap;
    }

    private void processLanguageTasks(TextItemData textItemData, ObservableList<Task> languageTasks) {
        for (var task : languageTasks) {
            if (task.selectedProperty().get()) {
                java.lang.reflect.Method method;
                java.lang.reflect.Field field;
                try {
                    var methodName = task.getId();
                    methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                    methodName = "set" + methodName;

                    method = textItemData.getClass().getMethod(methodName, int.class);
                    method.invoke(textItemData, wordClassesAsMap(languageTasks).get(task.getId()).getValue());

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
//                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "NoSuchMethodException"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
//                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "IllegalAccessException"));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
//                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "InvocationTargetException"));
                }
            }
        }
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

    protected TextItemData processTasks(ObservableList<Task> textTasks, ObservableList<Task> generalTasks, ObservableList<Task> languageTasks) {
        simpleTextAnalyzer.setDoc(doc);
        nlp.setDoc(doc);

        ObservableList<LinguisticFeature> featureList = FXCollections.observableArrayList();
        List<String> errorList = new ArrayList<>();

        TextItemData textItemData;

        if (language.equals(Locale.GERMAN) || language.equals(Locale.FRENCH) || language.equals(SupportedLocales.SPANISH.getLocale()))
            textItemData = new GermanTextItemData(doc.text());
        else
            textItemData = new EnglishTextItemData(doc.text());

        nlp.processTasks(textItemData, textTasks);
        simpleTextAnalyzer.processTasks(textItemData, generalTasks);
        processLanguageTasks(textItemData, languageTasks);

        return textItemData;
    }

    public List<String[]> syllableTest() {
//        simpleTextAnalyzer.setDoc(doc);
        ArrayList<String[]> outList = new ArrayList<>();
        outList.add("Word syllableCount".split(" "));


        for (var sentence : doc.sentences()) {
            System.out.println(sentence);
            sentence.words().forEach(word -> outList.add(String.format("%s %s", word.replace(",", "\",\""), SyllableProvider.getInstance().syllablesPerWord(word)).split(" ")));
        }

        return outList;
    }

    public Integer connectiveTest() {
        return ConnectivesProvider.getInstance().connectivesInDocument(doc);
    }
}
