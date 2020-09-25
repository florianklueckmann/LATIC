package dev.florianklueckmann.latic.services;

import edu.stanford.nlp.simple.Document;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class BaseTextAnalyzer implements TextAnalyzer {
    Document doc;

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    TextFormattingService textFormattingService;

    public BaseTextAnalyzer(TextFormattingService textFormatter) {
        this.textFormattingService = textFormatter;
    }
}
