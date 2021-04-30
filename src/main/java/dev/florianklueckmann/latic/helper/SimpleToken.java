package dev.florianklueckmann.latic.helper;

import edu.stanford.nlp.simple.Token;

public class SimpleToken {
    private String word;
    private String tag;

    public SimpleToken(String word, String tag) {
        setWord(word);
        setTag(tag);
    }

    public SimpleToken(Token token) {
        setWord(token.word());
        setTag(token.tag());
    }

    public String tag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String word() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
