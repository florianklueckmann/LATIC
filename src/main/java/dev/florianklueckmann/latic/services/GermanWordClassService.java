package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.IntegerLinguisticFeature;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedHashMap;

public class GermanWordClassService extends BaseWordClassService implements WordClassService {

    int adjectives = 0;
    int adpositions = 0;
    int adverbs = 0;
    int determiner = 0;
    int properNouns = 0;
    int modals = 0;
    int interjections = 0;
    int coordinatingConjunctions = 0;
    int subordinatingConjunctions = 0;
    int nouns = 0;
    int particles = 0;
    int pronouns = 0;
    int symbols = 0;
    int verbs = 0;
    int punctuations = 0;
    int numbers = 0;
    int unknown = 0;

    @Override
    public void countTags(String tag) {
        if (tag.contains("ADP")) {
            adpositions++;
            return;
        }
        if (tag.contains("CCONJ")) {
            coordinatingConjunctions++;
            return;
        }
        if (tag.contains("NUM")) {
            numbers++;
            return;
        }
        if (tag.contains("DET")) {
            determiner++;
            return;
        }

        if (tag.contains("SCONJ")) {
            subordinatingConjunctions++;
            return;
        }

        if (tag.contains("ADJ")) {
            adjectives++;
            return;
        }
        if (tag.contains("AUX")) {
            modals++;
            return;
        }

        if (tag.contains("NOUN")) {
            nouns++;
            return;
        }

        if (tag.contains("PROPN")) {
            properNouns++;
            return;
        }

        if (tag.contains("PRON")) {
            pronouns++;
            return;
        }

        if (tag.contains("ADV")) {
            adverbs++;
            return;
        }
        if (tag.contains("PART")) {
            particles++;
            return;
        }
        if (tag.contains("SYM")) {
            symbols++;
            return;
        }
        if (tag.contains("INTJ")) {
            interjections++;
            return;
        }
        if (tag.contains("VERB")) {
            verbs++;
            return;
        }
        if (tag.contains("PUNCT")) {
            punctuations++;
            return;
        }
        unknown++;
    }

    protected ObservableList<IntegerLinguisticFeature> createResultMap() {
//        var wordClassMap = new LinkedHashMap<String, Integer>();
        ObservableList<IntegerLinguisticFeature> outlist = FXCollections.observableArrayList();
        outlist.add(new IntegerLinguisticFeature("Adjectives", "adjectives", adjectives)  );
        outlist.add(new IntegerLinguisticFeature("Adpositions", "adpositions", adpositions)  );
        outlist.add(new IntegerLinguisticFeature("Adverbs", "adverbs", adverbs)  );
        outlist.add(new IntegerLinguisticFeature("Coordinating Conjunctions", "coordinatingConjunctions", coordinatingConjunctions)  );
        outlist.add(new IntegerLinguisticFeature("Subordinating Conjunctions", "subordinatingConjunctions", subordinatingConjunctions)  );
        outlist.add(new IntegerLinguisticFeature("Determiner", "determiner", determiner)  );
        outlist.add(new IntegerLinguisticFeature("Proper nouns", "properNouns", properNouns)  );
        outlist.add(new IntegerLinguisticFeature("Interjections", "interjections", interjections)  );
        outlist.add(new IntegerLinguisticFeature("Modal", "modals", modals)  );
        outlist.add(new IntegerLinguisticFeature("Nouns", "nouns", nouns)  );
        outlist.add(new IntegerLinguisticFeature("Numbers", "numbers", numbers)  );
        outlist.add(new IntegerLinguisticFeature("Particles", "particles", particles)  );
        outlist.add(new IntegerLinguisticFeature("Pronouns", "pronouns", pronouns)  );
        outlist.add(new IntegerLinguisticFeature("Symbols", "symbols", symbols)  );
        outlist.add(new IntegerLinguisticFeature("Verbs", "verbs", verbs)  );
        outlist.add(new IntegerLinguisticFeature("Punctuation", "punctuations", punctuations)  );
        outlist.add(new IntegerLinguisticFeature("Unknown", "unknown", unknown)  );

        return outlist;
    }
}
