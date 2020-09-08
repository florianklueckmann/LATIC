package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Document;

import java.util.Properties;

public class DocumentKeeper implements IDocumentKeeper {

    private Document doc;

    @Override
    public void initializeDocument(String input, Properties props) {
        this.doc = new Document(props, input);
    }

    @Override
    public Document getDocument() {
        return this.doc;
    }
}
