package dev.florianklueckmann.latic.word_class_service;

import dev.florianklueckmann.latic.linguistic_feature.IntegerLinguisticFeature;
import dev.florianklueckmann.latic.helper.TagMapper;
import edu.stanford.nlp.simple.Sentence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;

public abstract class BaseWordClassService implements WordClassService {

    @Override
    public ObservableList<IntegerLinguisticFeature> analyzeWordClasses(List<Sentence> sentences){
        sentences.forEach(sent -> TagMapper.getInstance().replaceTags(sent.tokens()).forEach(this::countTags));
        return createResultList();
    }

    @Override
    public ObservableMap<String, IntegerLinguisticFeature> wordClassesAsMap(List<Sentence> sentences){
        sentences.forEach(sent -> TagMapper.getInstance().replaceTags(sent.tokens()).forEach(this::countTags));
        ObservableMap<String, IntegerLinguisticFeature> outMap = FXCollections.observableHashMap();
        for(var element : createResultList()){
            outMap.put(element.getId(), element);
        }
        return outMap;
    }

    protected abstract ObservableList<IntegerLinguisticFeature> createResultList();
}