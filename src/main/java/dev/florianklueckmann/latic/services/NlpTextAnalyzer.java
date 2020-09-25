package dev.florianklueckmann.latic.services;

public class NlpTextAnalyzer extends BaseTextAnalyzer implements TextAnalyzer {

    public NlpTextAnalyzer(TextFormattingService textFormatter) {
        super(textFormatter);
    }
    public String textAndPosTags() {
        StringBuilder sb = new StringBuilder();
        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(sentence.posTags()).append("\n"));
        return sb.toString();
    }

    public String posTagsPerSentence() {
        StringBuilder sb = new StringBuilder();
        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(sentence.parse().taggedLabeledYield()).append("\n"));
        return sb.toString();
    }
}
