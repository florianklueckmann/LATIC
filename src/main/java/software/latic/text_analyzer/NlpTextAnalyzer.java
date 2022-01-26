package software.latic.text_analyzer;

import software.latic.item.TextItemData;
import software.latic.helper.TagMapper;
import software.latic.linguistic_feature.LinguisticFeature;
import software.latic.word_class_service.TextFormattingService;
import software.latic.task.Task;
import edu.stanford.nlp.simple.Token;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class NlpTextAnalyzer extends BaseTextAnalyzer implements TextAnalyzer {

    public static final NlpTextAnalyzer instance = new NlpTextAnalyzer();

    public static NlpTextAnalyzer getInstance() {
        return instance;
    }

    private NlpTextAnalyzer() {
    }

    public String textAndPosTags() {
        StringBuilder sb = new StringBuilder();
        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(replaceTags(sentence.tokens())).append("\n"));
        return sb.toString();
    }

    private List<String> replaceTags(List<Token> tokens) {
        return TagMapper.getInstance().replaceTags(tokens);
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

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
