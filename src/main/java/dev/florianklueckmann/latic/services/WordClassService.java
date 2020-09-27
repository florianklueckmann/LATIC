package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.IntegerLinguisticFeature;
import edu.stanford.nlp.simple.Sentence;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;

public interface WordClassService {

    ObservableList<IntegerLinguisticFeature> analyzeWordClasses(List<Sentence> sentences);
    ObservableMap<String, IntegerLinguisticFeature> wordClassesAsMap (List<Sentence> sentences);

    void countTags(String tag);
}
