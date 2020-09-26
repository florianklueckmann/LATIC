package dev.florianklueckmann.latic;

public enum TextInformation {
    TEXT_AND_POS_TAGS("Tagged text", "textAndPosTags"),
    POS_TAGS_PER_SENTENCE("Tagged sentences", "posTagsPerSentence");


    private final String name;

    public String getName() {
        return name;
    }

    private final String id;

    public String getId() {
        return id;
    }

    TextInformation(String name, String id) {
        this.name = name;
        this.id = id;
    }

}
