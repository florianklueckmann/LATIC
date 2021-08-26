package software.latic.translation;

import java.util.Locale;

public enum SupportedLocales {
    ENGLISH(Locale.ENGLISH),
    GERMAN(Locale.GERMAN),
    SPANISH(new Locale("es", "ES")),
    FRENCH(Locale.FRENCH);

    private final Locale locale;

    public Locale getLocale() {
        return locale;
    }

    SupportedLocales(Locale locale) {
        this.locale = locale;
    }
}
