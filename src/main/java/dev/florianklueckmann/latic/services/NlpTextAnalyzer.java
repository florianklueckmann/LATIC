package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.*;
import edu.stanford.nlp.simple.Token;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NlpTextAnalyzer extends BaseTextAnalyzer implements TextAnalyzer {

    public NlpTextAnalyzer(TextFormattingService textFormatter) {
        super(textFormatter);
    }
    public String textAndPosTags() {
        StringBuilder sb = new StringBuilder();
        //TODO if ListOfWordsToReplaceTags.contains(word) replace words tag with whatever. Maybe use a mapper?
        System.out.println(doc.sentences().get(0).tokens().get(0).word());
        System.out.println(doc.sentences().get(0).tokens().get(0).tag());
        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(replaceTags(sentence.tokens())).append("\n"));
        System.out.println(sb.toString());
        return sb.toString();
    }

    private List<String> replaceTags(List<Token> tokens) {
        return TagMapper.getInstance().replaceTags(tokens);
    }

    public String posTagsPerSentence() {
        StringBuilder sb = new StringBuilder();
        System.out.println(doc.sentences().get(0));
        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(sentence.parse().taggedLabeledYield()).append("\n"));
        return sb.toString();
    }

    @Override
    public void processTasks(TextItemData textItemData, ObservableList<Task> tasks) {
        setDoc(doc);

        ObservableList<LinguisticFeature> featureList = FXCollections.observableArrayList();
        List<String> errorList = new ArrayList<>();

        for (var task : tasks) {
            if (task.selectedProperty().get()) {
                java.lang.reflect.Method nlpMethod;
                java.lang.reflect.Method setter;
                try {
                    nlpMethod = getClass().getMethod(task.getId());

                    var setterName = task.getId();
                    setterName = setterName.substring(0, 1).toUpperCase() + setterName.substring(1);
                    setterName = "set" + setterName;
                    setter = textItemData.getClass().getMethod(setterName, String.class);


                    setter.invoke(textItemData, String.valueOf(nlpMethod.invoke(this)));

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
}
