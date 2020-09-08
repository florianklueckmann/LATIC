package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Document;

import java.util.Properties;

public interface IDocumentKeeper {

    void initializeDocument(String input, Properties props);

    Document getDocument();

}
