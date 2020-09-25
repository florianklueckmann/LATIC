package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.IntegerLinguisticFeature;
import dev.florianklueckmann.latic.LinguisticFeature;
import edu.stanford.nlp.simple.Sentence;
import javafx.collections.ObservableList;

import java.util.List;

public abstract class BaseWordClassService implements WordClassService {

    @Override
    public ObservableList<IntegerLinguisticFeature> analyzeWordClasses(List<Sentence> sentences){
        sentences.forEach(sent -> sent.posTags().forEach(this::countTags));
        return createResultMap();
    }

    protected abstract ObservableList<IntegerLinguisticFeature> createResultMap();

}
