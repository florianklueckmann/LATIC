package dev.florianklueckmann.latic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Arrays;
import java.util.stream.Stream;

public class EnglishTextItemData extends TextItemData
{
    IntegerProperty adverbs;
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

    IntegerProperty adjectives;
    IntegerProperty conjunctions;
    IntegerProperty existentialThere;
    IntegerProperty foreignWords;
    IntegerProperty listItemMarkers;
    IntegerProperty possessiveEndings;
    IntegerProperty prepositionOrSubordinatingConjunction;
    IntegerProperty to;

    public EnglishTextItemData(String text)
    {
        super(text);

        this.adverbs = new SimpleIntegerProperty();
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

        this.adjectives = new SimpleIntegerProperty();
        this.conjunctions = new SimpleIntegerProperty();
        this.existentialThere = new SimpleIntegerProperty();
        this.foreignWords = new SimpleIntegerProperty();
        this.listItemMarkers = new SimpleIntegerProperty();
        this.possessiveEndings = new SimpleIntegerProperty();
        this.prepositionOrSubordinatingConjunction = new SimpleIntegerProperty();
        this.to = new SimpleIntegerProperty();
    }

    public String[] getValues() {

        String[] superValues = super.getValues();
        String[] values = new String[]{
                String.valueOf(getAdjectives()),
                String.valueOf(getConjunctions()),
                String.valueOf(getPrepositionOrSubordinatingConjunction()),
                String.valueOf(getAdverbs()),
                String.valueOf(getDeterminer()),
                String.valueOf(getInterjections()),
                String.valueOf(getModals()),
                String.valueOf(getNouns()),
                String.valueOf(getNumbers()),
                String.valueOf(getParticles()),
                String.valueOf(getPronouns()),
                String.valueOf(getProperNouns()),
                String.valueOf(getSymbols()),
                String.valueOf(getVerbs()),
                String.valueOf(getPunctuation()),
                String.valueOf(getExistentialThere()),
                String.valueOf(getListItemMarkers()),
                String.valueOf(getPossessiveEndings()),
                String.valueOf(getTo()),
                String.valueOf(getUnknown())
        };

        return Stream.concat(Arrays.stream(superValues), Arrays.stream(values))
                .toArray(String[]::new);
    }

    public int getConjunctions()
    {
        return conjunctions.get();
    }

    public IntegerProperty conjunctionsProperty()
    {
        return conjunctions;
    }

    public void setConjunctions(int conjunctions)
    {
        this.conjunctions.set(conjunctions);
    }

    public int getExistentialThere()
    {
        return existentialThere.get();
    }

    public IntegerProperty existentialThereProperty()
    {
        return existentialThere;
    }

    public void setExistentialThere(int existentialThere)
    {
        this.existentialThere.set(existentialThere);
    }

    public int getForeignWords()
    {
        return foreignWords.get();
    }

    public IntegerProperty foreignWordsProperty()
    {
        return foreignWords;
    }

    public void setForeignWords(int foreignWords)
    {
        this.foreignWords.set(foreignWords);
    }

    public int getListItemMarkers()
    {
        return listItemMarkers.get();
    }

    public IntegerProperty listItemMarkersProperty()
    {
        return listItemMarkers;
    }

    public void setListItemMarkers(int listItemMarkers)
    {
        this.listItemMarkers.set(listItemMarkers);
    }

    public int getPossessiveEndings()
    {
        return possessiveEndings.get();
    }

    public IntegerProperty possessiveEndingsProperty()
    {
        return possessiveEndings;
    }

    public void setPossessiveEndings(int possessiveEndings)
    {
        this.possessiveEndings.set(possessiveEndings);
    }

    public int getPrepositionOrSubordinatingConjunction()
    {
        return prepositionOrSubordinatingConjunction.get();
    }

    public IntegerProperty prepositionOrSubordinatingConjunctionProperty()
    {
        return prepositionOrSubordinatingConjunction;
    }

    public void setPrepositionOrSubordinatingConjunction(int prepositionOrSubordinatingConjunction)
    {
        this.prepositionOrSubordinatingConjunction.set(prepositionOrSubordinatingConjunction);
    }

    public int getTo()
    {
        return to.get();
    }

    public IntegerProperty toProperty()
    {
        return to;
    }

    public void setTo(int to)
    {
        this.to.set(to);
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
}
