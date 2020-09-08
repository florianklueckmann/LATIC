package dev.florianklueckmann.latic;

public class TestItem {

    String text;
    int wordCount;
    int sentenceCount;
    double avgSentenceLengthWords;
    double avgWordLengthChar;
    double avgSentLengthChar;


    public TestItem(String text, int wordCount, int sentenceCount, double avgSentenceLengthWords, double avgWordLengthChar, double avgSentLengthChar) {
        this.text = text;
        this.wordCount = wordCount;
        this.sentenceCount = sentenceCount;
        this.avgSentenceLengthWords = avgSentenceLengthWords;
        this.avgWordLengthChar = avgWordLengthChar;
        this.avgSentLengthChar = avgSentLengthChar;
    }

    public String getText() {
        return text;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    public double getAvgSentenceLengthWords() {
        return avgSentenceLengthWords;
    }

    public double getAvgWordLengthChar() {
        return avgWordLengthChar;
    }

    public double getAvgSentLengthChar() {
        return avgSentLengthChar;
    }
}
