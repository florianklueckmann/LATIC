package software.latic.helper;

import software.latic.App;

import java.util.prefs.Preferences;

public class Settings {
    public static final Preferences userPreferences = Preferences.userNodeForPackage(App.class);
}
