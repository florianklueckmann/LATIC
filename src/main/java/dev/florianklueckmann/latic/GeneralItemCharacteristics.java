package dev.florianklueckmann.latic;

public enum GeneralItemCharacteristics {

//    TEXT("Text","text"),
//    TEXT_AND_POS_TAGS("Tagged text","textAndPosTags"),
//    POS_TAGS_PER_SENTENCE("Tagged sentences","posTagsPerSentence"),
    WORD_COUNT("Word count","wordCount"),
    AVERAGE_WORD_LENGTH_CHAR("Average word length characters","averageWordLengthCharacters"),
    SENTENCE_COUNT("Sentence Count","sentenceCount"),
    AVERAGE_SENTENCE_LENGTH_CHARACTERS("Average sentence length characters","averageSentenceLengthCharacters"),
    AVERAGE_SENTENCE_LENGTH_CHARACTERS_WITHOUT_WHITESPACES("Average sentence length characters without whitespaces","averageSentenceLengthCharactersWithoutWhitespaces"),
    AVERAGE_SENTENCE_LENGTH_WORDS("Average sentence length words","averageSentenceLengthWords"),
    LEXICAL_DIVERSITY("Lexical Diversity","lexicalDiversity"),
    LIX_SCORE("LIX readability score","lixReadabilityScore");

    private final String name;

    public String getName() {
        return name;
    }

    private final String id;

    public String getId() {
        return id;
    }

    GeneralItemCharacteristics(String name, String id) {
        this.name = name;
        this.id = id;
    }

}
