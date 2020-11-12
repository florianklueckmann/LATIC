package dev.florianklueckmann.latic;

public enum GeneralItemCharacteristics {

//    TEXT("Text","text"),
//    TEXT_AND_POS_TAGS("Tagged text","textAndPosTags"),
//    POS_TAGS_PER_SENTENCE("Tagged sentences","posTagsPerSentence"),
    WORD_COUNT("Word count","wordCount"),
    AVERAGE_WORD_LENGTH_CHAR("Word length (characters)","averageWordLengthCharacters"),
    SENTENCE_COUNT("Number of sentences","sentenceCount"),
    AVERAGE_SENTENCE_LENGTH_CHARACTERS("Sentence length (characters)","averageSentenceLengthCharacters"),
    AVERAGE_SENTENCE_LENGTH_CHARACTERS_WITHOUT_WHITESPACES("Sentence length (characters w/o spaces)","averageSentenceLengthCharactersWithoutWhitespaces"),
    AVERAGE_SENTENCE_LENGTH_WORDS("Sentence length (words)","averageSentenceLengthWords"),
    LEXICAL_DIVERSITY("Lexical Diversity","lexicalDiversity"),
    LIX_SCORE("LIX","lixReadabilityScore");

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
