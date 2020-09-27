package dev.florianklueckmann.latic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GermanTextItemData extends TextItemData
{
    IntegerProperty adjectives;
    IntegerProperty adpositions;
    IntegerProperty adverbs;
    IntegerProperty coordinatingConjunctions;
    IntegerProperty subordinatingConjunctions;
    IntegerProperty determiner;
    IntegerProperty interjections;
    IntegerProperty modals;
    IntegerProperty nouns;
    IntegerProperty numbers;
    IntegerProperty particles;
    IntegerProperty pronouns;
    IntegerProperty properNouns;
    IntegerProperty symbols;
    IntegerProperty verbs;
    IntegerProperty punctuation;
    IntegerProperty unknown;

    public GermanTextItemData(String text,
                              String textAndPosTags,
                              String posTagsPerSentence,
                              Integer wordCount,
                              Double averageWordLengthCharacters,
                              Integer sentenceCount,
                              Double averageSentenceLengthCharacters,
                              Double averageSentenceLengthCharactersWithoutWhitespaces,
                              Double averageSentenceLengthWords,
                              Double lexicalDiversity,
                              Double lixReadabilityScore,
                              Integer adjectives,
                              Integer adpositions,
                              Integer adverbs,
                              Integer coordinatingConjunctions,
                              Integer subordinatingConjunctions,
                              Integer determiner,
                              Integer interjections,
                              Integer modals,
                              Integer nouns,
                              Integer numbers,
                              Integer particles,
                              Integer pronouns,
                              Integer properNouns,
                              Integer symbols,
                              Integer verbs,
                              Integer punctuation,
                              Integer unknown)
    {
        super(text, textAndPosTags, posTagsPerSentence, wordCount, averageWordLengthCharacters, sentenceCount, averageSentenceLengthCharacters, averageSentenceLengthCharactersWithoutWhitespaces, averageSentenceLengthWords, lexicalDiversity, lixReadabilityScore);
        this.adjectives = new SimpleIntegerProperty(adjectives);
        this.adpositions = new SimpleIntegerProperty(adpositions);
        this.adverbs = new SimpleIntegerProperty(adverbs);
        this.coordinatingConjunctions = new SimpleIntegerProperty(coordinatingConjunctions);
        this.subordinatingConjunctions = new SimpleIntegerProperty(subordinatingConjunctions);
        this.determiner = new SimpleIntegerProperty(determiner);
        this.interjections = new SimpleIntegerProperty(interjections);
        this.modals = new SimpleIntegerProperty(modals);
        this.nouns = new SimpleIntegerProperty(nouns);
        this.numbers = new SimpleIntegerProperty(numbers);
        this.particles = new SimpleIntegerProperty(particles);
        this.pronouns = new SimpleIntegerProperty(pronouns);
        this.properNouns = new SimpleIntegerProperty(properNouns);
        this.symbols = new SimpleIntegerProperty(symbols);
        this.verbs = new SimpleIntegerProperty(verbs);
        this.punctuation = new SimpleIntegerProperty(punctuation);
        this.unknown = new SimpleIntegerProperty(unknown);
    }

    public GermanTextItemData(IntegerProperty adjectives, IntegerProperty adpositions, IntegerProperty adverbs, IntegerProperty coordinatingConjunctions, IntegerProperty subordinatingConjunctions, IntegerProperty determiner, IntegerProperty interjections, IntegerProperty modals, IntegerProperty nouns, IntegerProperty numbers, IntegerProperty particles, IntegerProperty pronouns, IntegerProperty properNouns, IntegerProperty symbols, IntegerProperty verbs, IntegerProperty punctuation, IntegerProperty unknown)
    {
        this.adjectives = adjectives;
        this.adpositions = adpositions;
        this.adverbs = adverbs;
        this.coordinatingConjunctions = coordinatingConjunctions;
        this.subordinatingConjunctions = subordinatingConjunctions;
        this.determiner = determiner;
        this.interjections = interjections;
        this.modals = modals;
        this.nouns = nouns;
        this.numbers = numbers;
        this.particles = particles;
        this.pronouns = pronouns;
        this.properNouns = properNouns;
        this.symbols = symbols;
        this.verbs = verbs;
        this.punctuation = punctuation;
        this.unknown = unknown;
    }

    public GermanTextItemData()
    {
        super();
        this.adjectives = new SimpleIntegerProperty();
        this.adpositions = new SimpleIntegerProperty();
        this.adverbs = new SimpleIntegerProperty();
        this.coordinatingConjunctions = new SimpleIntegerProperty();
        this.subordinatingConjunctions = new SimpleIntegerProperty();
        this.determiner = new SimpleIntegerProperty();
        this.interjections = new SimpleIntegerProperty();
        this.modals = new SimpleIntegerProperty();
        this.nouns = new SimpleIntegerProperty();
        this.numbers = new SimpleIntegerProperty();
        this.particles = new SimpleIntegerProperty();
        this.pronouns = new SimpleIntegerProperty();
        this.properNouns = new SimpleIntegerProperty();
        this.symbols = new SimpleIntegerProperty();
        this.verbs = new SimpleIntegerProperty();
        this.punctuation = new SimpleIntegerProperty();
        this.unknown = new SimpleIntegerProperty();
    }

    public int getAdjectives()
    {
        return adjectives.get();
    }

    public IntegerProperty adjectivesProperty()
    {
        return adjectives;
    }

    public void setAdjectives(int adjectives)
    {
        this.adjectives.set(adjectives);
    }

    public int getAdpositions()
    {
        return adpositions.get();
    }

    public IntegerProperty adpositionsProperty()
    {
        return adpositions;
    }

    public void setAdpositions(int adpositions)
    {
        this.adpositions.set(adpositions);
    }

    public int getAdverbs()
    {
        return adverbs.get();
    }

    public IntegerProperty adverbsProperty()
    {
        return adverbs;
    }

    public void setAdverbs(int adverbs)
    {
        this.adverbs.set(adverbs);
    }

    public int getCoordinatingConjunctions()
    {
        return coordinatingConjunctions.get();
    }

    public IntegerProperty coordinatingConjunctionsProperty()
    {
        return coordinatingConjunctions;
    }

    public void setCoordinatingConjunctions(int coordinatingConjunctions)
    {
        this.coordinatingConjunctions.set(coordinatingConjunctions);
    }

    public int getSubordinatingConjunctions()
    {
        return subordinatingConjunctions.get();
    }

    public IntegerProperty subordinatingConjunctionsProperty()
    {
        return subordinatingConjunctions;
    }

    public void setSubordinatingConjunctions(int subordinatingConjunctions)
    {
        this.subordinatingConjunctions.set(subordinatingConjunctions);
    }

    public int getDeterminer()
    {
        return determiner.get();
    }

    public IntegerProperty determinerProperty()
    {
        return determiner;
    }

    public void setDeterminer(int determiner)
    {
        this.determiner.set(determiner);
    }

    public int getInterjections()
    {
        return interjections.get();
    }

    public IntegerProperty interjectionsProperty()
    {
        return interjections;
    }

    public void setInterjections(int interjections)
    {
        this.interjections.set(interjections);
    }

    public int getModals()
    {
        return modals.get();
    }

    public IntegerProperty modalsProperty()
    {
        return modals;
    }

    public void setModals(int modals)
    {
        this.modals.set(modals);
    }

    public int getNouns()
    {
        return nouns.get();
    }

    public IntegerProperty nounsProperty()
    {
        return nouns;
    }

    public void setNouns(int nouns)
    {
        this.nouns.set(nouns);
    }

    public int getNumbers()
    {
        return numbers.get();
    }

    public IntegerProperty numbersProperty()
    {
        return numbers;
    }

    public void setNumbers(int numbers)
    {
        this.numbers.set(numbers);
    }

    public int getParticles()
    {
        return particles.get();
    }

    public IntegerProperty particlesProperty()
    {
        return particles;
    }

    public void setParticles(int particles)
    {
        this.particles.set(particles);
    }

    public int getPronouns()
    {
        return pronouns.get();
    }

    public IntegerProperty pronounsProperty()
    {
        return pronouns;
    }

    public void setPronouns(int pronouns)
    {
        this.pronouns.set(pronouns);
    }

    public int getProperNouns()
    {
        return properNouns.get();
    }

    public IntegerProperty properNounsProperty()
    {
        return properNouns;
    }

    public void setProperNouns(int properNouns)
    {
        this.properNouns.set(properNouns);
    }

    public int getSymbols()
    {
        return symbols.get();
    }

    public IntegerProperty symbolsProperty()
    {
        return symbols;
    }

    public void setSymbols(int symbols)
    {
        this.symbols.set(symbols);
    }

    public int getVerbs()
    {
        return verbs.get();
    }

    public IntegerProperty verbsProperty()
    {
        return verbs;
    }

    public void setVerbs(int verbs)
    {
        this.verbs.set(verbs);
    }

    public int getPunctuation()
    {
        return punctuation.get();
    }

    public IntegerProperty punctuationProperty()
    {
        return punctuation;
    }

    public void setPunctuation(int punctuation)
    {
        this.punctuation.set(punctuation);
    }

    public int getUnknown()
    {
        return unknown.get();
    }

    public IntegerProperty unknownProperty()
    {
        return unknown;
    }

    public void setUnknown(int unknown)
    {
        this.unknown.set(unknown);
    }
}
