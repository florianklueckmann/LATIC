package software.latic.readability_indices.v2;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import software.latic.linguistic_feature.DoubleLinguisticFeature;
import software.latic.readability_indices.ReadabilityIndices;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Facade for the decoupled Readability V2 feature.
 * This feature is disabled by default and can be enabled via
 *  -Dlatic.features.readability.v2=true (or env LATIC_FEATURES_READABILITY_V2=true)
 */
public final class ReadabilityV2Feature {

    private ReadabilityV2Feature() { }

    public static ObservableList<DoubleLinguisticFeature> compute(Document document) {
        List<Sentence> sentences = document == null ? List.of() : document.sentences();
        return compute(sentences);
    }

    public static ObservableList<DoubleLinguisticFeature> compute(List<Sentence> sentences) {
        if (!FeatureToggle.enabled()) {
            return FXCollections.observableArrayList();
        }
        // Load providers via ServiceLoader only when enabled to keep feature decoupled
        ServiceLoader<ReadabilityIndices> loader = ServiceLoader.load(ReadabilityIndices.class);
        List<DoubleLinguisticFeature> all = new ArrayList<>();
        for (ReadabilityIndices provider : loader) {
            ObservableList<DoubleLinguisticFeature> result = provider.calculateReadabilityIndices(sentences);
            if (result != null) {
                all.addAll(result);
            }
        }
        return FXCollections.observableArrayList(all);
    }
}
