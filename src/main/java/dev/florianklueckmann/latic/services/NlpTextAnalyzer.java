package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.LinguisticFeature;
import dev.florianklueckmann.latic.Task;
import dev.florianklueckmann.latic.TextItemData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class NlpTextAnalyzer extends BaseTextAnalyzer implements TextAnalyzer {

    public NlpTextAnalyzer(TextFormattingService textFormatter) {
        super(textFormatter);
    }
    public String textAndPosTags() {
        StringBuilder sb = new StringBuilder();
        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(sentence.posTags()).append("\n"));
        return sb.toString();
    }

    public String posTagsPerSentence() {
        StringBuilder sb = new StringBuilder();
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

    public ObservableList<String[]> getWordTagPairList(){
        ObservableList<String[]> list = FXCollections.observableArrayList();
        for (var sentence: doc.sentences()) {
            for (int i = 0; i < sentence.length(); i++) {
                list.add(new String[] {sentence.word(i), sentence.posTag(i)});
            }
        }
        return list;
    }
}
