package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Sentence;

import java.util.List;
import java.util.Map;

public interface WordClassService {

    Map<String, Integer> analyzeWordClasses(List<Sentence> sentences);

    void countTags(String tag);

//    Map<String, Integer> createResultMap();
}
