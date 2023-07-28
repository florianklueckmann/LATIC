package software.latic;


import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
    private static final Logging instance = new Logging();

    public static Logging getInstance() {
        return instance;
    }

    public void debug(String name, String message) {
        Logger.getLogger(name).log(Level.INFO, message);
    }

    public void warn(String name, String message) {
        Logger.getLogger(name).log(Level.WARNING, message);
    }
}
