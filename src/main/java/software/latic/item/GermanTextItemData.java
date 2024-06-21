package software.latic.item;

import javafx.beans.property.*;
import javafx.collections.ObservableMap;

public class GermanTextItemData extends TextItemData {
    IntegerProperty adjectives;
    IntegerProperty adpositions;
    IntegerProperty conjunctions;
    IntegerProperty coordinatingConjunctions;
    IntegerProperty subordinatingConjunctions;
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

    DoubleProperty averageWordFrequencyClass;
    DoubleProperty fleschIndexGerman;
    StringProperty fleschIndexGermanLevel;
    DoubleProperty wienerSachtextformel;
    StringProperty wienerSachtextformelLevel;
    DoubleProperty gSMOG;
    StringProperty gSMOGLevel;


    public GermanTextItemData(String text) {

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
        this.adpositions = new SimpleIntegerProperty();
        this.conjunctions = new SimpleIntegerProperty();
        this.coordinatingConjunctions = new SimpleIntegerProperty();
        this.subordinatingConjunctions = new SimpleIntegerProperty();

        this.averageWordFrequencyClass = new RoundedDoubleProperty();
        this.fleschIndexGerman = new RoundedDoubleProperty();
        this.fleschIndexGermanLevel = new SimpleStringProperty();
        this.wienerSachtextformel = new RoundedDoubleProperty();
        this.wienerSachtextformelLevel = new SimpleStringProperty();
        this.gSMOG = new RoundedDoubleProperty();
        this.gSMOGLevel = new SimpleStringProperty();
    }

    @Override
    public ObservableMap<String, String> getIdValueMap() {
        var valueMap = super.getIdValueMap();
        valueMap.put("adjectives",String.valueOf(getAdjectives()));
        valueMap.put("adpositions",String.valueOf(getAdpositions()));
        valueMap.put("conjunctions",String.valueOf(getConjunctions()));
        valueMap.put("coordinatingConjunctions",String.valueOf(getCoordinatingConjunctions()));
        valueMap.put("subordinatingConjunctions",String.valueOf(getSubordinatingConjunctions()));
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
        valueMap.put("punctuation",String.valueOf(getPunctuation()));
        valueMap.put("unknown",String.valueOf(getUnknown()));

        valueMap.put("averageWordFrequencyClass",String.valueOf(getAverageWordFrequencyClass()));
        valueMap.put("fleschIndexGerman",String.valueOf(getFleschIndexGerman()));
        valueMap.put("fleschIndexGermanLevel", getFleschIndexGermanLevel());
        valueMap.put("wienerSachtextformel",String.valueOf(getWienerSachtextformel()));
        valueMap.put("wienerSachtextformelLevel", getWienerSachtextformelLevel());
        valueMap.put("gSMOG",String.valueOf(getGSMOG()));
        valueMap.put("gSMOGLevel", getgSMOGLevel());

        return valueMap;
    }

    public int getAdpositions() {
        return adpositions.get();
    }

    public IntegerProperty adpositionsProperty() {
        return adpositions;
    }

    public void setAdpositions(int adpositions) {
        this.adpositions.set(adpositions);
    }

    public int getConjunctions() {
        return conjunctions.get();
    }

    public IntegerProperty conjunctionsProperty() {
        return conjunctions;
    }

    public void setConjunctions(int conjunctions) {
        this.conjunctions.set(conjunctions);
    }

    public int getCoordinatingConjunctions() {
        return coordinatingConjunctions.get();
    }

    public IntegerProperty coordinatingConjunctionsProperty() {
        return coordinatingConjunctions;
    }

    public void setCoordinatingConjunctions(int coordinatingConjunctions) {
        this.coordinatingConjunctions.set(coordinatingConjunctions);
    }

    public int getSubordinatingConjunctions() {
        return subordinatingConjunctions.get();
    }

    public IntegerProperty subordinatingConjunctionsProperty() {
        return subordinatingConjunctions;
    }

    public void setSubordinatingConjunctions(int subordinatingConjunctions) {
        this.subordinatingConjunctions.set(subordinatingConjunctions);
    }

    public int getAdverbs() {
        return adverbs.get();
    }

    public IntegerProperty adverbsProperty() {
        return adverbs;
    }

    public void setAdverbs(int adverbs) {
        this.adverbs.set(adverbs);
    }

    public int getDeterminer() {
        return determiner.get();
    }

    public IntegerProperty determinerProperty() {
        return determiner;
    }

    public void setDeterminer(int determiner) {
        this.determiner.set(determiner);
    }

    public int getInterjections() {
        return interjections.get();
    }

    public IntegerProperty interjectionsProperty() {
        return interjections;
    }

    public void setInterjections(int interjections) {
        this.interjections.set(interjections);
    }

    public int getModals() {
        return modals.get();
    }

    public IntegerProperty modalsProperty() {
        return modals;
    }

    public void setModals(int modals) {
        this.modals.set(modals);
    }

    public int getNouns() {
        return nouns.get();
    }

    public IntegerProperty nounsProperty() {
        return nouns;
    }

    public void setNouns(int nouns) {
        this.nouns.set(nouns);
    }

    public int getNumbers() {
        return numbers.get();
    }

    public IntegerProperty numbersProperty() {
        return numbers;
    }

    public void setNumbers(int numbers) {
        this.numbers.set(numbers);
    }

    public int getParticles() {
        return particles.get();
    }

    public IntegerProperty particlesProperty() {
        return particles;
    }

    public void setParticles(int particles) {
        this.particles.set(particles);
    }

    public int getPronouns() {
        return pronouns.get();
    }

    public IntegerProperty pronounsProperty() {
        return pronouns;
    }

    public void setPronouns(int pronouns) {
        this.pronouns.set(pronouns);
    }

    public int getProperNouns() {
        return properNouns.get();
    }

    public IntegerProperty properNounsProperty() {
        return properNouns;
    }

    public void setProperNouns(int properNouns) {
        this.properNouns.set(properNouns);
    }

    public int getSymbols() {
        return symbols.get();
    }

    public IntegerProperty symbolsProperty() {
        return symbols;
    }

    public void setSymbols(int symbols) {
        this.symbols.set(symbols);
    }

    public int getVerbs() {
        return verbs.get();
    }

    public IntegerProperty verbsProperty() {
        return verbs;
    }

    public void setVerbs(int verbs) {
        this.verbs.set(verbs);
    }

    public int getPunctuation() {
        return punctuation.get();
    }

    public IntegerProperty punctuationProperty() {
        return punctuation;
    }

    public void setPunctuation(int punctuation) {
        this.punctuation.set(punctuation);
    }

    public int getUnknown() {
        return unknown.get();
    }

    public IntegerProperty unknownProperty() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown.set(unknown);
    }

    public int getAdjectives() {
        return adjectives.get();
    }

    public IntegerProperty adjectivesProperty() {
        return adjectives;
    }

    public void setAdjectives(int adjectives) {
        this.adjectives.set(adjectives);
    }

    public double getFleschIndexGerman() {
        return fleschIndexGerman.get();
    }

    public DoubleProperty fleschIndexGermanProperty() {
        return fleschIndexGerman;
    }

    public void setFleschIndexGerman(double fleschIndex) {
        this.fleschIndexGerman.set(fleschIndex);
    }

    public double getWienerSachtextformel() {
        return wienerSachtextformel.get();
    }

    public DoubleProperty wienerSachtextformelProperty() {
        return wienerSachtextformel;
    }

    public void setWienerSachtextformel(double wienerSachtextformel) {
        this.wienerSachtextformel.set(wienerSachtextformel);
    }

    public double getGSMOG() {
        return gSMOG.get();
    }

    public DoubleProperty gSMOGProperty() {
        return gSMOG;
    }

    public void setGSMOG(double gSMOG) {
        this.gSMOG.set(gSMOG);
    }

    public double getAverageWordFrequencyClass() {
        return averageWordFrequencyClass.get();
    }

    public DoubleProperty averageWordFrequencyClassProperty() {
        return averageWordFrequencyClass;
    }

    public void setAverageWordFrequencyClass(double averageWordFrequency) {
        this.averageWordFrequencyClass.set(averageWordFrequency);
    }

    public String getgSMOGLevel() {
        return gSMOGLevel.get();
    }

    public StringProperty gSMOGLevelProperty() {
        return gSMOGLevel;
    }

    public void setgSMOGLevel(String gSMOGLevel) {
        this.gSMOGLevel.set(gSMOGLevel);
    }

    public String getWienerSachtextformelLevel() {
        return wienerSachtextformelLevel.get();
    }

    public StringProperty wienerSachtextformelLevelProperty() {
        return wienerSachtextformelLevel;
    }

    public void setWienerSachtextformelLevel(String wienerSachtextformelLevel) {
        this.wienerSachtextformelLevel.set(wienerSachtextformelLevel);
    }

    public String getFleschIndexGermanLevel() {
        return fleschIndexGermanLevel.get();
    }

    public StringProperty fleschIndexGermanLevelProperty() {
        return fleschIndexGermanLevel;
    }

    public void setFleschIndexGermanLevel(String fleschIndexGermanLevel) {
        this.fleschIndexGermanLevel.set(fleschIndexGermanLevel);
    }
}
