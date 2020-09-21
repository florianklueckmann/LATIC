package dev.florianklueckmann.latic.services;

import java.util.LinkedHashMap;
import java.util.Map;

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

    protected Map<String, Integer> createResultMap() {
        var wordClassMap = new LinkedHashMap<String, Integer>();
        wordClassMap.put("Adjectives", adjectives);
        wordClassMap.put("Adpositions", adpositions);
        wordClassMap.put("Adverbs", adverbs);
        wordClassMap.put("Coordinating Conjunctions", coordinatingConjunctions);
        wordClassMap.put("Subordinating Conjunctions", subordinatingConjunctions);
        wordClassMap.put("Determiner", determiner);
        wordClassMap.put("Proper nouns", properNouns);
        wordClassMap.put("Interjections", interjections);
        wordClassMap.put("Modal", modals);
        wordClassMap.put("Nouns", nouns);
        wordClassMap.put("Numbers", numbers);
        wordClassMap.put("Particles", particles);
        wordClassMap.put("Pronouns", pronouns);
        wordClassMap.put("Symbols", symbols);
        wordClassMap.put("Verbs", verbs);
        wordClassMap.put("Punctuation", punctuations);
        wordClassMap.put("Unknown", unknown);

        return wordClassMap;
    }
}
