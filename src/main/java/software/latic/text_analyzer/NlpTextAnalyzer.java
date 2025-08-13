package software.latic.text_analyzer;

import edu.stanford.nlp.trees.Tree;
import software.latic.Logging;
import software.latic.item.TextItemData;
import software.latic.helper.TagMapper;
import software.latic.linguistic_feature.LinguisticFeature;
import software.latic.task.Task;
import edu.stanford.nlp.simple.Token;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NlpTextAnalyzer extends BaseTextAnalyzer implements TextAnalyzer {

    final List<String> knownNounTags = List.of(
            "NOUN",
            "PROPN",
            "MPN",
            "NP",
            "NNP"
    );

    public static final NlpTextAnalyzer instance = new NlpTextAnalyzer();

    public static NlpTextAnalyzer getInstance() {
        return instance;
    }

    private NlpTextAnalyzer() {
    }

    //TODO only parse sentence once and save data

    public String textAndPosTags() {

        var firstSent = doc.sentences().getFirst();

        var parsed = doc.sentences().getFirst().parse();
        System.out.println("PARSED" + parsed.toString());

        var dep = firstSent.incomingDependencyLabels();

        StringBuilder deps = new StringBuilder();

        for (var s : dep) {
            deps.append(s).append(" ");
        }

        System.out.println("DEP" + deps);

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

    public int nounPhrasesCount() {
        AtomicLong count = new AtomicLong(0);
        var nounPhrases = new ArrayList<String>();

        doc.sentences().forEach(sentence -> {
            var tree = sentence.parse();
            Logger.getLogger("NlpTextAnalyzer").log(Level.INFO, tree.toString());

            int additionalNPs = findNounSequences(tree, new HashSet<>(), nounPhrases);

            count.addAndGet(additionalNPs);
        });
        Logging.getInstance().debug("NlpTextAnalyzer", String.format("nounPhrases: %s", nounPhrases));
        return count.intValue();
    }

    private boolean labelIsNounPhrase(String labelValue) {
        return labelValue.equalsIgnoreCase("NP")
                || labelValue.equalsIgnoreCase("NP-TMP")
                || labelValue.equalsIgnoreCase("WHNP")
                || labelValue.equalsIgnoreCase("NPS");
    }

    private boolean isSimpleNounPhrase(Tree tree) {
        return labelIsNounPhrase(tree.label().value()) && doesNotContainNP(tree);
    }

    private boolean isPPWithNoun(Tree tree) {
        return tree.label().value().equalsIgnoreCase("PP")
                && containsNoun(tree) && doesNotContainNP(tree);
    }

    private int findNounSequences(Tree tree, Set<Tree> counted, List<String> nounPhrases) {
        int count = 0;

        if (counted.contains(tree) || tree.depth() <= 1) {
            return 0;
        }

        //If we find a NP -> NP SBAR sequence, we assume that SBAR does always describe the Subject in NP
        if ((tree.label().value().equalsIgnoreCase("NP") && (containsOnlyNPAndSBAR(tree) || (containsNoun(tree) && containsPP(tree))))) {
            count++;
            nounPhrases.add(tree.toString());
            counted.addAll(Arrays.asList(tree.children()));
        } else if (isPPWithNoun(tree) || isSimpleNounPhrase(tree)) {
            count++;
            nounPhrases.add(tree.toString());
            counted.add(tree);
        }

        for (Tree child : tree.children()) {
            count += findNounSequences(child, counted, nounPhrases);
        }

        return count;
    }

    private boolean containsOnlyNPAndSBAR(Tree tree) {
        var hasSBAR = new AtomicBoolean(false);
        var hasNP = new AtomicBoolean(false);

        if (tree.children().length == 2) {
            Arrays.stream(tree.children()).forEach(child -> {
                if (child.value().equalsIgnoreCase("NP")) {
                    hasNP.set(true);
                } else if (child.value().equalsIgnoreCase("SBAR")) {
                    hasSBAR.set(true);
                }
            });
        }
        return hasSBAR.get() && hasNP.get();
    }

    private boolean containsNoun(Tree tree) {
        return Arrays.stream(tree.children()).anyMatch(child -> knownNounTags.contains(child.label().value()));
    }

    private boolean containsPP(Tree tree) {
        return Arrays.stream(tree.children()).anyMatch(child -> child.label().value().equalsIgnoreCase("PP"));
    }

    private boolean doesNotContainNP(Tree tree) {
        return Arrays.stream(tree.children()).noneMatch(child -> Objects.equals(child.label().value(), "NP"));
    }

    public int passiveConstructionsCount() {
        AtomicInteger count = new AtomicInteger(0);

        doc.sentences().forEach(sentence -> {
            var labels = sentence.incomingDependencyLabels().stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            Logging.getInstance().debug("NlpTextAnalyzer", String.format("labels: %s", labels));

            int pairsInSentence = (int) Math.min(
                    labels.stream().filter("nsubj:pass"::equals).count(),
                    labels.stream().filter("aux:pass"::equals).count()
            );

            int agentsInSentence = (int) labels.stream().filter(e -> e.equalsIgnoreCase("obl:agent") || e.equalsIgnoreCase("obl:by")).count();

            if (pairsInSentence > 0) {
                count.addAndGet((int) Math.max(pairsInSentence, agentsInSentence));
            }

            Logger.getLogger("NlpTextAnalyzer").log(Level.INFO,
                    String.format("%d pairs and %d agents in sentence %s", pairsInSentence, agentsInSentence, sentence.text()));
        });

        Logger.getLogger("NlpTextAnalyzer").log(Level.INFO,
                String.format("Total passive constructions: %d", count.get()));
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

                    setter = textItemData.getClass().getMethod(setterName, nlpMethod.getReturnType());

                    setter.invoke(textItemData, nlpMethod.invoke(this));

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
