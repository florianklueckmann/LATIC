package dev.florianklueckmann.latic;

public abstract class LinguisticFeatureBase implements LinguisticFeature {

    String name;
    String id;

    public LinguisticFeatureBase(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
