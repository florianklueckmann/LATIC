package dev.florianklueckmann.latic.text_analyzer;

import dev.florianklueckmann.latic.word_class_service.TextFormattingService;
import edu.stanford.nlp.simple.Document;

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
