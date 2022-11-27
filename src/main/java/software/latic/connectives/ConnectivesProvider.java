package software.latic.connectives;

import software.latic.translation.Translation;

import java.util.Locale;

public class ConnectivesProvider
{
    public static Connectives getInstance() {
        if (Translation.getInstance().getLocale().equals(Locale.GERMAN)) {
            return BaseConnectives.getInstance();
        } else {
            return BaseConnectives.getInstance();
        }
    }
}
