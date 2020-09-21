package dev.florianklueckmann.latic.services;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishWordClassService extends BaseWordClassService implements WordClassService {
    private int adjectives = 0;
    private int adverbs = 0;
    private int conjunctions = 0;
    private int determiner = 0;
    private int existentialThere = 0;
    private int foreignWords = 0;
    private int interjections = 0;
    private int listItemMarkers = 0;
    private int modal = 0;
    private int nouns = 0;
    private int numbers = 0;
    private int particles = 0;
    private int possessiveEndings = 0;
    private int prepositionOrSubordinatingConjunction = 0;
    private int pronouns = 0;
    private int symbols = 0;
    private int verbs = 0;
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
        if (tag.contains("FW")) {
            foreignWords++;
            return;
        }

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
            modal++;
            return;
        }

        if (tag.contains("NN")) {
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

        if (tag.contains("RB")) {
            adverbs++;
            return;
        }
        if (tag.contains("RP")) {
            particles++;
            return;
        }
        if (tag.contains("SYM")) {
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
            return;
        }
        if (tag.equals(".")) {
            punctuations++;
            return;
        }
        unknown++;
    }

    protected Map<String, Integer> createResultMap() {
        var wordClassMap = new LinkedHashMap<String, Integer>();
        wordClassMap.put("Adjectives", adjectives);
        wordClassMap.put("Adverbs", adverbs);
        wordClassMap.put("Conjunctions", conjunctions);
        wordClassMap.put("Determiner", determiner);
        wordClassMap.put("Existential there", existentialThere);
        wordClassMap.put("Foreign words", foreignWords);
        wordClassMap.put("Interjections", interjections);
        wordClassMap.put("List item markers", listItemMarkers);
        wordClassMap.put("Modal", modal);
        wordClassMap.put("Nouns", nouns);
        wordClassMap.put("Numbers", numbers);
        wordClassMap.put("Particles", particles);
        wordClassMap.put("Possessive endings", possessiveEndings);
        wordClassMap.put("Preposition or subordinating conjunction", prepositionOrSubordinatingConjunction);
        wordClassMap.put("Pronouns", pronouns);
        wordClassMap.put("Symbols", symbols);
        wordClassMap.put("Verbs", verbs);
        wordClassMap.put("to", to);
        wordClassMap.put("Punctuation", punctuations);
        wordClassMap.put("Unknown", unknown);

        return wordClassMap;
    }
}
