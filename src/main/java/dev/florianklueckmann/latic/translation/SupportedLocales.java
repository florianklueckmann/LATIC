package dev.florianklueckmann.latic.translation;

import java.util.Locale;

public enum SupportedLocales {
    ENGLISH(Locale.ENGLISH),
    GERMAN(Locale.GERMAN);

    private final Locale locale;

    public Locale getLocale() {
        return locale;
    }

    SupportedLocales(Locale locale) {
        this.locale = locale;
    }
}
