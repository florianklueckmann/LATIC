package software.latic.translation;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Translation {
    private static final Translation translation = new Translation();

    private ObjectProperty<Locale> locale;

    private Translation() {
        var userLocale = getSupportedLocales().stream().filter(l -> l.getLanguage().equals(Locale.getDefault().getLanguage())).findFirst();
        locale = new SimpleObjectProperty<>(userLocale.isPresent() ? userLocale.get() : getSupportedLocales().get(0));
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    public static Translation getInstance() {
        return translation;
    }

    public Locale getLocale() {
        return locale.get();
    }
    public void setLocale(Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }

    public ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public List<Locale> getSupportedLocales() {
        return Arrays.asList(
                SupportedLocales.ENGLISH.getLocale(),
                SupportedLocales.GERMAN.getLocale(),
                SupportedLocales.SPANISH.getLocale(),
                SupportedLocales.FRENCH.getLocale()
        );
    }

    public boolean canAnalyzeSyllablesForLocale() {
        return getLocale().equals(SupportedLocales.ENGLISH.getLocale())
                || getLocale().equals(SupportedLocales.GERMAN.getLocale());
    }

    public String getTranslation(final String key){
        ResourceBundle bundle = ResourceBundle.getBundle("software.latic.messages", getLocale());
        try {
            return bundle.getString(key);
        }catch (MissingResourceException e) {
            Logger.getLogger("Translation").log(Level.WARNING, String.format("Missing translation in locale '%s': %s. Using key '%s' as fallback.", getLocale().getLanguage(),e.getMessage(), e.getKey()) );
            return key;
        }

    }

    public StringBinding createStringBinding(final String key) {
        return Bindings.createStringBinding(() -> getTranslation(key), locale);
    }

    public String getLanguageTag() {
        return getLocale().toLanguageTag().toLowerCase(Locale.ROOT);
    }

    public String getDocumentationFileName() {
        if (getLocale() == SupportedLocales.GERMAN.getLocale()) {
            return "documentation_de.pdf";
        } else {
            return "documentation_en.pdf";
        }
    }
}
