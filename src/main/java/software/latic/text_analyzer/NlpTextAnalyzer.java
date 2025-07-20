package software.latic.text_analyzer;

import software.latic.item.TextItemData;
import software.latic.helper.TagMapper;
import software.latic.linguistic_feature.LinguisticFeature;
import software.latic.task.Task;
import edu.stanford.nlp.simple.Token;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NlpTextAnalyzer extends BaseTextAnalyzer implements TextAnalyzer {

    public static final NlpTextAnalyzer instance = new NlpTextAnalyzer();

    public static NlpTextAnalyzer getInstance() {
        return instance;
    }

    private NlpTextAnalyzer() {
    }

    //TODO only parse sentence once and save data

    public String textAndPosTags() {
        StringBuilder sb = new StringBuilder();
        doc.sentences().forEach(sentence -> {
            TagMapper.getInstance().replaceTagsInTokenList(sentence.tokens()).forEach(token -> sb
                    .append(token.word())
                    .append(" [")
                    .append(token.tag())
                    .append("] "));
            sb.append("\n");
        });
        return sb.toString().trim();
    }

    public Integer passiveConstructionsCount() {
        AtomicInteger count = new AtomicInteger(0);

        doc.sentences().forEach(sentence -> {
            var labels = sentence.incomingDependencyLabels().stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            int pairsInSentence = (int) Math.min(
                    labels.stream().filter("nsubj:pass"::equals).count(),
                    labels.stream().filter("aux:pass"::equals).count()
            );

            count.addAndGet(pairsInSentence);

            Logger.getLogger("NlpTextAnalyzer").log(Level.INFO,
                    String.format("sentence: %s", sentence.text()));
            Logger.getLogger("NlpTextAnalyzer").log(Level.INFO,
                    String.format("pairs in sentence: %d", pairsInSentence));
        });

        Logger.getLogger("NlpTextAnalyzer").log(Level.INFO,
                String.format("Total passive construction pairs: %d", count.get()));
        return count.get();

    }

    public String passiveConstructions() {
        StringBuilder sb = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);

        doc.sentences().forEach(sentence -> {
            var labels = sentence.incomingDependencyLabels().stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            int pairsInSentence = (int) Math.min(
                    labels.stream().filter("nsubj:pass"::equals).count(),
                    labels.stream().filter("aux:pass"::equals).count()
            );

            count.addAndGet(pairsInSentence);

            sentence.incomingDependencyLabels().forEach(label -> {
                label.ifPresent(l -> sb.append(l).append(" "));
            });
            sb.append("\n");

            Logger.getLogger("NlpTextAnalyzer").log(Level.INFO,
                    String.format("sentence: %s", sentence.text()));
            Logger.getLogger("NlpTextAnalyzer").log(Level.INFO,
                    String.format("pairs in sentence: %d", pairsInSentence));
        });

        var result = sb.toString().trim();
        Logger.getLogger("NlpTextAnalyzer").log(Level.INFO,
                String.format("Total passive construction pairs: %d", count.get()));
        return result.isEmpty() ? "" : result;

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

                    var resultClass = task.getResultType().orElse(String.class);

                    setter = textItemData.getClass().getMethod(setterName, resultClass);

                    setter.invoke(textItemData, nlpMethod.invoke(this));

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
