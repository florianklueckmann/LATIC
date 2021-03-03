package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.IntegerLinguisticFeature;
import dev.florianklueckmann.latic.TagMapper;
import edu.stanford.nlp.simple.Sentence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;

public abstract class BaseWordClassService implements WordClassService {

    @Override
    public ObservableList<IntegerLinguisticFeature> analyzeWordClasses(List<Sentence> sentences){
        sentences.forEach(sent -> TagMapper.getInstance().replaceTags(sent.tokens()).forEach(this::countTags));
//        sentences.forEach(sent -> sent.posTags().forEach(this::countTags));
        return createResultList();
    }

    @Override
    public ObservableMap<String, IntegerLinguisticFeature> wordClassesAsMap(List<Sentence> sentences){
        sentences.forEach(sent -> TagMapper.getInstance().replaceTags(sent.tokens()).forEach(this::countTags));
//        sentences.forEach(sent -> sent.posTags().forEach(this::countTags));
        ObservableMap<String, IntegerLinguisticFeature> outMap = FXCollections.observableHashMap();
        for(var element : createResultList()){
            outMap.put(element.getId(), element);
        }
        return outMap;
    }

    protected abstract ObservableList<IntegerLinguisticFeature> createResultList();

}
