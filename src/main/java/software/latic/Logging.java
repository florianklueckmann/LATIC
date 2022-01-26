package software.latic;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Logging {
    private static final Logging instance = new Logging();
    public static Logging getInstance() {
        return instance;
    }

    public Logging() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(App.loggingLevel);
    }

    public void debug(String name, String message) {
        Logger.getLogger(name).debug(message);
    }

    public void warn(String name, String message) {
        Logger.getLogger(name).warn(message);
    }
}
