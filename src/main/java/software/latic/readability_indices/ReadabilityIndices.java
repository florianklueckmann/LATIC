package software.latic.readability_indices;

import edu.stanford.nlp.simple.Sentence;
import javafx.collections.ObservableList;
import software.latic.linguistic_feature.DoubleLinguisticFeature;

import java.util.List;

public interface ReadabilityIndices {
    ObservableList<DoubleLinguisticFeature> calculateReadabilityIndices(List<Sentence> sentences);
}
