package software.latic.frequency;

import java.util.Objects;

public final class FrequencyListEntry {
    private final String word;
    private int frequency;

    public FrequencyListEntry(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public String word() {
        return word;
    }

    public int frequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FrequencyListEntry) obj;
        return Objects.equals(this.word, that.word) &&
                this.frequency == that.frequency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, frequency);
    }

    @Override
    public String toString() {
        return "FrequencyListEntry[" +
                "word=" + word + ", " +
                "frequency=" + frequency + ']';
    }


}
