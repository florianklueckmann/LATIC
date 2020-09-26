package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Document;

public interface TextAnalyzer
{
    Document getDoc();

    void setDoc(Document doc);
}
