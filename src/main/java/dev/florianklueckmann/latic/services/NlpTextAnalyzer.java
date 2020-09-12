package dev.florianklueckmann.latic.services;

public class NlpTextAnalyzer extends BaseTextAnalyzer {

    public NlpTextAnalyzer(TextFormattingService textFormatter) {
        super(textFormatter);
    }

    public String posTagsAsString() {
        StringBuilder sb = new StringBuilder();
        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(sentence.posTags()).append("\n"));
        return sb.toString();
    }

    public String textAndPosTagsAsString() {
        StringBuilder sb = new StringBuilder();
        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(sentence.parse().taggedLabeledYield()).append("\n"));
        return sb.toString();
    }
}
