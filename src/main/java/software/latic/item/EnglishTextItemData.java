package software.latic.item;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableMap;

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
    IntegerProperty verbsBaseForm = new SimpleIntegerProperty();
    IntegerProperty verbsGerundOrPresentParticiple = new SimpleIntegerProperty();
    IntegerProperty verbsPastTense = new SimpleIntegerProperty();
    IntegerProperty verbsPastParticiple = new SimpleIntegerProperty();
    IntegerProperty verbsNonThirdSingularPresent = new SimpleIntegerProperty();
    IntegerProperty verbsThirdSingularPresent = new SimpleIntegerProperty();
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

    DoubleProperty fleschIndexEnglish;
    DoubleProperty fleschKincaid;
    DoubleProperty gunningFox;
    DoubleProperty automatedReadabilityIndex;
    DoubleProperty colemanLiau;
    DoubleProperty SMOG;

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

        this.fleschIndexEnglish = new RoundedDoubleProperty();
        this.fleschKincaid = new RoundedDoubleProperty();
        this.gunningFox = new RoundedDoubleProperty();
        this.automatedReadabilityIndex = new RoundedDoubleProperty();
        this.colemanLiau = new RoundedDoubleProperty();
        this.SMOG = new RoundedDoubleProperty();
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
                String.valueOf(getVerbsBaseForm()),
                String.valueOf(getVerbsPastTense()),
                String.valueOf(getVerbsGerundOrPresentParticiple()),
                String.valueOf(getVerbsPastParticiple()),
                String.valueOf(getVerbsNonThirdSingularPresent()),
                String.valueOf(getVerbsThirdSingularPresent()),
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

    @Override
    public ObservableMap<String, String> getIdValueMap() {
        var valueMap = super.getIdValueMap();
        valueMap.put("adjectives",String.valueOf(getAdjectives()));
        valueMap.put("conjunctions",String.valueOf(getConjunctions()));
        valueMap.put("prepositionOrSubordinatingConjunction",String.valueOf(getPrepositionOrSubordinatingConjunction()));
        valueMap.put("adverbs",String.valueOf(getAdverbs()));
        valueMap.put("determiner",String.valueOf(getDeterminer()));
        valueMap.put("interjections",String.valueOf(getInterjections()));
        valueMap.put("modals",String.valueOf(getModals()));
        valueMap.put("nouns",String.valueOf(getNouns()));
        valueMap.put("numbers",String.valueOf(getNumbers()));
        valueMap.put("particles",String.valueOf(getParticles()));
        valueMap.put("pronouns",String.valueOf(getPronouns()));
        valueMap.put("properNouns",String.valueOf(getProperNouns()));
        valueMap.put("symbols",String.valueOf(getSymbols()));
        valueMap.put("verbs",String.valueOf(getVerbs()));
        valueMap.put("verbsBaseForm",String.valueOf(getVerbsBaseForm()));
        valueMap.put("verbsPastTense",String.valueOf(getVerbsPastTense()));
        valueMap.put("verbsGerundOrPresentParticiple",String.valueOf(getVerbsGerundOrPresentParticiple()));
        valueMap.put("verbsPastParticiple",String.valueOf(getVerbsPastParticiple()));
        valueMap.put("verbsNonThirdSingularPresent",String.valueOf(getVerbsNonThirdSingularPresent()));
        valueMap.put("verbsThirdSingularPresent",String.valueOf(getVerbsThirdSingularPresent()));
        valueMap.put("punctuation",String.valueOf(getPunctuation()));
        valueMap.put("existentialThere",String.valueOf(getExistentialThere()));
        valueMap.put("listItemMarkers",String.valueOf(getListItemMarkers()));
        valueMap.put("possessiveEndings",String.valueOf(getPossessiveEndings()));
        valueMap.put("to",String.valueOf(getTo()));
        valueMap.put("unknown",String.valueOf(getUnknown()));

        valueMap.put("fleschIndexEnglish",String.valueOf(getFleschIndexEnglish()));
        valueMap.put("fleschKincaid",String.valueOf(getFleschKincaid()));
        valueMap.put("gunningFox",String.valueOf(getGunningFox()));
        valueMap.put("automatedReadabilityIndex",String.valueOf(getAutomatedReadabilityIndex()));
        valueMap.put("colemanLiau",String.valueOf(getColemanLiau()));
        valueMap.put("SMOG",String.valueOf(getSMOG()));

        return valueMap;
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

    public int getVerbsBaseForm() {
        return verbsBaseForm.get();
    }

    public IntegerProperty verbsBaseFormProperty() {
        return verbsBaseForm;
    }

    public void setVerbsBaseForm(int verbsBaseForm) {
        this.verbsBaseForm.set(verbsBaseForm);
    }

    public int getVerbsGerundOrPresentParticiple() {
        return verbsGerundOrPresentParticiple.get();
    }

    public IntegerProperty verbsGerundOrPresentParticipleProperty() {
        return verbsGerundOrPresentParticiple;
    }

    public void setVerbsGerundOrPresentParticiple(int verbsGerundOrPresentParticiple) {
        this.verbsGerundOrPresentParticiple.set(verbsGerundOrPresentParticiple);
    }

    public int getVerbsPastTense() {
        return verbsPastTense.get();
    }

    public IntegerProperty verbsPastTenseProperty() {
        return verbsPastTense;
    }

    public void setVerbsPastTense(int verbsPastTense) {
        this.verbsPastTense.set(verbsPastTense);
    }

    public int getVerbsPastParticiple() {
        return verbsPastParticiple.get();
    }

    public IntegerProperty verbsPastParticipleProperty() {
        return verbsPastParticiple;
    }

    public void setVerbsPastParticiple(int verbsPastParticiple) {
        this.verbsPastParticiple.set(verbsPastParticiple);
    }

    public int getVerbsNonThirdSingularPresent() {
        return verbsNonThirdSingularPresent.get();
    }

    public IntegerProperty verbsNonThirdSingularPresentProperty() {
        return verbsNonThirdSingularPresent;
    }

    public void setVerbsNonThirdSingularPresent(int verbsNonThirdSingularPresent) {
        this.verbsNonThirdSingularPresent.set(verbsNonThirdSingularPresent);
    }

    public int getVerbsThirdSingularPresent() {
        return verbsThirdSingularPresent.get();
    }

    public IntegerProperty verbsThirdSingularPresentProperty() {
        return verbsThirdSingularPresent;
    }

    public void setVerbsThirdSingularPresent(int verbsThirdSingularPresent) {
        this.verbsThirdSingularPresent.set(verbsThirdSingularPresent);
    }

    public double getFleschIndexEnglish() {
        return fleschIndexEnglish.get();
    }

    public DoubleProperty fleschIndexEnglishProperty() {
        return fleschIndexEnglish;
    }

    public void setFleschIndexEnglish(double fleschIndex) {
        this.fleschIndexEnglish.set(fleschIndex);
    }

    public double getFleschKincaid() {
        return fleschKincaid.get();
    }

    public DoubleProperty fleschKincaidProperty() {
        return fleschKincaid;
    }

    public void setFleschKincaid(double fleschKincaid) {
        this.fleschKincaid.set(fleschKincaid);
    }

    public double getGunningFox() {
        return gunningFox.get();
    }

    public DoubleProperty gunningFoxProperty() {
        return gunningFox;
    }

    public void setGunningFox(double gunningFox) {
        this.gunningFox.set(gunningFox);
    }

    public double getAutomatedReadabilityIndex() {
        return automatedReadabilityIndex.get();
    }

    public DoubleProperty automatedReadabilityIndexProperty() {
        return automatedReadabilityIndex;
    }

    public void setAutomatedReadabilityIndex(double automatedReadabilityIndex) {
        this.automatedReadabilityIndex.set(automatedReadabilityIndex);
    }

    public double getColemanLiau() {
        return colemanLiau.get();
    }

    public DoubleProperty colemanLiauProperty() {
        return colemanLiau;
    }

    public void setColemanLiau(double colemanLiau) {
        this.colemanLiau.set(colemanLiau);
    }

    public double getSMOG() {
        return SMOG.get();
    }

    public DoubleProperty SMOGProperty() {
        return SMOG;
    }

    public void setSMOG(double SMOG) {
        this.SMOG.set(SMOG);
    }
}
