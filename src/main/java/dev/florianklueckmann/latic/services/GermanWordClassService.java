package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.IntegerLinguisticFeature;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    protected ObservableList<IntegerLinguisticFeature> createResultList() {

        return FXCollections.observableArrayList(
                new IntegerLinguisticFeature("Adjectives", "adjectives", adjectives),
                new IntegerLinguisticFeature("Adpositions", "adpositions", adpositions),
                new IntegerLinguisticFeature("Adverbs", "adverbs", adverbs),
                new IntegerLinguisticFeature("Coordinating Conjunctions", "coordinatingConjunctions", coordinatingConjunctions),
                new IntegerLinguisticFeature("Subordinating Conjunctions", "subordinatingConjunctions", subordinatingConjunctions),
                new IntegerLinguisticFeature("Determiner", "determiner", determiner),
                new IntegerLinguisticFeature("Proper nouns", "properNouns", properNouns),
                new IntegerLinguisticFeature("Interjections", "interjections", interjections),
                new IntegerLinguisticFeature("Modal", "modals", modals),
                new IntegerLinguisticFeature("Nouns", "nouns", nouns),
                new IntegerLinguisticFeature("Numbers", "numbers", numbers),
                new IntegerLinguisticFeature("Particles", "particles", particles),
                new IntegerLinguisticFeature("Pronouns", "pronouns", pronouns),
                new IntegerLinguisticFeature("Symbols", "symbols", symbols),
                new IntegerLinguisticFeature("Verbs", "verbs", verbs),
                new IntegerLinguisticFeature("Punctuation", "punctuation", punctuations),
                new IntegerLinguisticFeature("Unknown", "unknown", unknown)
        );
    }
}
