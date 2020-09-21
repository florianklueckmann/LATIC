package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Document;

import java.util.Properties;

public class DocumentKeeper {

    private Document doc;

    public void initializeDocument(String input, Properties props) {
        this.doc = new Document(props, input);
    }

    public Document getDocument() {
        return this.doc;
    }
}
