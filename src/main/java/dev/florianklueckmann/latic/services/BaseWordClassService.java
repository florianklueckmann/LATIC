package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Sentence;

import java.util.List;
import java.util.Map;

public abstract class BaseWordClassService implements WordClassService {

    @Override
    public Map<String, Integer> analyzeWordClasses(List<Sentence> sentences){
        sentences.forEach(sent -> sent.posTags().forEach(this::countTags));
        return createResultMap();
    }

    protected abstract Map<String, Integer> createResultMap();

}
