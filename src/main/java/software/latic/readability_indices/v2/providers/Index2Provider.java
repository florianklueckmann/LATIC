package software.latic.readability_indices.v2.providers;

import edu.stanford.nlp.simple.Sentence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import software.latic.linguistic_feature.DoubleLinguisticFeature;
import software.latic.readability_indices.ReadabilityIndices;

import java.util.List;

/**
 * Placeholder for new readability index #2.
 */
public class Index2Provider implements ReadabilityIndices {
    public static final String ID = "readabilityV2.index2";

    @Override
    public ObservableList<DoubleLinguisticFeature> calculateReadabilityIndices(List<Sentence> sentences) {
        // TODO: implement real calculation later
        return FXCollections.observableArrayList(
                new DoubleLinguisticFeature("Readability V2 Index 2", ID, -1.0)
        );
    }
}
