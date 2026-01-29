package software.latic.readability_indices;

import edu.stanford.nlp.simple.Document;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.latic.linguistic_feature.DoubleLinguisticFeature;
import software.latic.readability_indices.v2.ReadabilityV2Feature;

import java.util.List;
import java.util.Locale;

class ReadabilityV2FeatureTest {

    @Test
    void disabledByDefault_returnsEmpty() {
        // Ensure property is not set
        System.clearProperty("latic.features.readability.v2");
        ObservableList<DoubleLinguisticFeature> res = ReadabilityV2Feature.compute(List.of());
        Assertions.assertNotNull(res);
        Assertions.assertEquals(0, res.size());
    }

    @Test
    void enabled_viaSystemProperty_loadsFiveProviders() {
        System.setProperty("latic.features.readability.v2", "true");
        try {
            Document doc = new Document("Short text.");
            ObservableList<DoubleLinguisticFeature> res = ReadabilityV2Feature.compute(doc);
            Assertions.assertEquals(5, res.size());
            // Verify IDs are present
            Assertions.assertTrue(res.stream().anyMatch(f -> f.getId().equals("readabilityV2.index1")));
            Assertions.assertTrue(res.stream().anyMatch(f -> f.getId().equals("readabilityV2.index2")));
            Assertions.assertTrue(res.stream().anyMatch(f -> f.getId().equals("readabilityV2.index3")));
            Assertions.assertTrue(res.stream().anyMatch(f -> f.getId().equals("readabilityV2.index4")));
            Assertions.assertTrue(res.stream().anyMatch(f -> f.getId().equals("readabilityV2.index5")));
        } finally {
            System.clearProperty("latic.features.readability.v2");
        }
    }
}
