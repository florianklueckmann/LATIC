package software.latic.connectives;

public class Connective {
    String word;
    int start;
    int end;

    public Connective(String word, int start, int end) {
        this.word = word;
        this.start = start;
        this.end = end;
    }

    public String getWord() {
        return word;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return this.word + " " + this.start + " " + this.end;
    }
}
