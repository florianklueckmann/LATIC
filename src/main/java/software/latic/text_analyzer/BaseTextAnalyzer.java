package software.latic.text_analyzer;

import software.latic.word_class_service.TextFormattingService;
import edu.stanford.nlp.simple.Document;

public abstract class BaseTextAnalyzer implements TextAnalyzer {
    Document doc;

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    TextFormattingService textFormattingService = TextFormattingService.getInstance();

    public BaseTextAnalyzer() {
    }
}
