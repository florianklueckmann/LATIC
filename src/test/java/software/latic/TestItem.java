package software.latic;

public class TestItem {

    String text;
    int wordCount;
    int sentenceCount;
    int syllableCount;
    double avgSentenceLengthWords;
    double avgWordLengthChar;
    double avgWordLengthSyll;
    double avgSentLengthChar;
    double avgSentLengthSyll;


    public TestItem(String text, int wordCount, int sentenceCount, int syllableCount, double avgSentenceLengthWords,
                    double avgWordLengthChar, double avgSentLengthChar, double avgSentLengthSyll, double avgWordLengthSyll) {
        this.text = text;
        this.wordCount = wordCount;
        this.sentenceCount = sentenceCount;
        this.syllableCount = syllableCount;
        this.avgSentenceLengthWords = avgSentenceLengthWords;
        this.avgWordLengthChar = avgWordLengthChar;
        this.avgWordLengthSyll = avgWordLengthSyll;
        this.avgSentLengthChar = avgSentLengthChar;
        this.avgSentLengthSyll = avgSentLengthSyll;
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

    public int getSyllableCount() {return syllableCount;}

    public double getAvgSentenceLengthWords() {
        return avgSentenceLengthWords;
    }

    public double getAverageSentenceLengthSyllables() {return avgSentLengthSyll;}

    public double getAvgWordLengthChar() {
        return avgWordLengthChar;
    }

    public double getAvgSentLengthChar() {
        return avgSentLengthChar;
    }

    public double getAvgWordLengthSyll() {return avgWordLengthSyll;}

    public double getAvgSentLengthSyll() {return avgSentLengthSyll;}
}
