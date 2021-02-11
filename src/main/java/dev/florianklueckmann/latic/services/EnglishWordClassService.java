package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.IntegerLinguisticFeature;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EnglishWordClassService extends BaseWordClassService implements WordClassService {
    private int adjectives = 0;
    private int adverbs = 0;
    private int conjunctions = 0;
    private int determiner = 0;
    private int existentialThere = 0;
//    private int foreignWords = 0;
    private int interjections = 0;
    private int listItemMarkers = 0;
    private int modals = 0;
    private int nouns = 0;
    private int numbers = 0;
    private int particles = 0;
    private int possessiveEndings = 0;
    private int prepositionOrSubordinatingConjunction = 0;
    private int pronouns = 0;
    private int properNouns = 0;
    private int symbols = 0;
    private int verbs = 0;
    private int verbsBaseForm = 0;
    private int verbsGerundOrPresentParticiple = 0;
    private int verbsPastTense = 0;
    private int verbsPastParticiple = 0;
    private int verbsNonThirdSingularPresent = 0;
    private int verbsThirdSingularPresent = 0;
    private int to = 0;
    private int punctuations = 0;
    private int unknown = 0;

    @Override
    public void countTags(String tag) {
        if (tag.contains("CC")) {
            conjunctions++;
            return;
        }
        if (tag.contains("CD")) {
            numbers++;
            return;
        }
        if (tag.contains("DT")) {
            determiner++;
            return;
        }
        if (tag.contains("EX")) {
            existentialThere++;
            return;
        }

//        if (tag.contains("FW")) {
//            foreignWords++;
//            return;
//        }

        if (tag.contains("IN")) {
            prepositionOrSubordinatingConjunction++;
            return;
        }

        if (tag.contains("JJ")) {
            adjectives++;
            return;
        }

        if (tag.contains("LS")) {
            listItemMarkers++;
            return;
        }

        if (tag.contains("MD")) {
            modals++;
            return;
        }

        if (tag.contains("NN") && !tag.equals("NNP")) {
            nouns++;
            return;
        }

        if (tag.contains("POS")) {
            possessiveEndings++;
            return;
        }

        if (tag.contains("PRP") || tag.contains("WP")) {
            pronouns++;
            return;
        }

        if (tag.equals("NNP")) {
            properNouns++;
            return;
        }

        if (tag.contains("RB") && !tag.contains("-")) {
            adverbs++;
            return;
        }
        if (tag.contains("RP")) {
            particles++;
            return;
        }
        if (tag.contains("SYM") || tag.equals("$")) {
            symbols++;
            return;
        }
        if (tag.contains("TO")) {
            to++;
            return;
        }
        if (tag.contains("UH")) {
            interjections++;
            return;
        }
        if (tag.contains("VB")) {
            verbs++;
            if (tag.equalsIgnoreCase("VB"))
                verbsBaseForm++;
            else if (tag.equalsIgnoreCase("VBD"))
                verbsPastTense++;
            else if (tag.equalsIgnoreCase("VBG"))
                verbsGerundOrPresentParticiple++;
            else if (tag.equalsIgnoreCase("VBN"))
                verbsPastParticiple++;
            else if (tag.equalsIgnoreCase("VBP"))
                verbsNonThirdSingularPresent++;
            else if (tag.equalsIgnoreCase("VBZ"))
                verbsThirdSingularPresent++;
            return;
        }
        if (
                tag.equals(".") ||
                tag.equals(",") ||
                tag.equals(":") ||
                tag.equals("\"") ||
                tag.equals("-RRB-") ||
                tag.equals("-LRB-") ||
                tag.equals("``") ||
                tag.equals("''") ||
                tag.equals("HYPH") ||
                tag.equals("NFP")
        ) {
            punctuations++;
            return;
        }
        unknown++;
    }

    protected ObservableList<IntegerLinguisticFeature> createResultList() {
        return FXCollections.observableArrayList(
                new IntegerLinguisticFeature("Adjectives", "adjectives", adjectives),
                new IntegerLinguisticFeature("Adverbs", "adverbs", adverbs),
                new IntegerLinguisticFeature("Conjunctions", "conjunctions", conjunctions),
                new IntegerLinguisticFeature("Determiner", "determiner", determiner),
                new IntegerLinguisticFeature("Existential there", "existentialThere", existentialThere),
//                new IntegerLinguisticFeature("Foreign words", "foreignWords", foreignWords),
                new IntegerLinguisticFeature("Interjections", "interjections", interjections),
                new IntegerLinguisticFeature("List item markers", "listItemMarkers", listItemMarkers),
                new IntegerLinguisticFeature("Modal", "modals", modals),
                new IntegerLinguisticFeature("Nouns", "nouns", nouns),
                new IntegerLinguisticFeature("Numbers", "numbers", numbers),
                new IntegerLinguisticFeature("Particles", "particles", particles),
                new IntegerLinguisticFeature("Possessive endings", "possessiveEndings", possessiveEndings),
                new IntegerLinguisticFeature("Preposition or subordinating conjunction", "prepositionOrSubordinatingConjunction", prepositionOrSubordinatingConjunction),
                new IntegerLinguisticFeature("Pronouns", "pronouns", pronouns),
                new IntegerLinguisticFeature("Proper nouns", "properNouns", properNouns),
                new IntegerLinguisticFeature("Symbols", "symbols", symbols),
                new IntegerLinguisticFeature("Verbs", "verbs", verbs),
                new IntegerLinguisticFeature("Verbs Base Form", "verbsBaseForm", verbsBaseForm),
                new IntegerLinguisticFeature("Verbs Past Tense", "verbsPastTense", verbsPastTense),
                new IntegerLinguisticFeature("Verbs Gerund/PresentParticiple", "verbsGerundOrPresentParticiple", verbsGerundOrPresentParticiple),
                new IntegerLinguisticFeature("Verbs Past Participle", "verbsPastParticiple", verbsPastParticiple),
                new IntegerLinguisticFeature("Verbs non Third Person Singular Present", "verbsNonThirdSingularPresent", verbsNonThirdSingularPresent),
                new IntegerLinguisticFeature("Verbs Third Person Singular Present", "verbsThirdSingularPresent", verbsThirdSingularPresent),
                new IntegerLinguisticFeature("to", "to", to),
                new IntegerLinguisticFeature("Punctuation", "punctuation", punctuations),
                new IntegerLinguisticFeature("Unknown", "unknown", unknown)
        );
    }
}
