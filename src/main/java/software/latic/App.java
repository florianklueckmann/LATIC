package software.latic;

import software.latic.helper.UpdateHelper;
import software.latic.translation.Translation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;


/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    protected static final Level loggingLevel = Level.INFO;
    protected static ResourceBundle bundle = null;

    @Override
    public void start(Stage stage) throws IOException {
        Locale.setDefault(Locale.GERMAN);
        Translation.getInstance().setLocale(Locale.GERMAN);
        prepareStage(stage);
        stage.show();
        UpdateHelper.getInstance().updateCheck();

        Logging.getInstance().debug("App", String.format("JavaFX runtime: %s", System.getProperty("javafx.runtime.version")));
        Logging.getInstance().debug("App", String.format("Java runtime: %s", System.getProperty("java.runtime.version")));
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        try {
            bundle = ResourceBundle.getBundle("software.latic.messages", Translation.getInstance().getLocale());
        }
        catch (MissingResourceException e){
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"), bundle);

        return fxmlLoader.load();
    }

    private static void prepareStage(Stage stage) throws IOException {
        scene = new Scene(loadFXML("PrimaryView"), 1000, 600);
        scene.getStylesheets().add("/software/latic/main.css");

        var imageResourceStream = App.class.getResourceAsStream("latic-square-256.png");
        if (imageResourceStream != null) {
            stage.getIcons().add(new Image(imageResourceStream));
        }

        stage.setTitle("LATIC " + UpdateHelper.getInstance().getCurrentVersion());
        stage.setScene(scene);

    }

    private static String getAppVersion() {
        var appBundle = ResourceBundle.getBundle("software.latic.app");
        return appBundle.getString("version");
    }

    public static void main(String[] args) {
        launch();
    }

    public static Level getLoggingLevel() {
        return loggingLevel;
    }
}

/**
 * This class is used as a workaround to start the application.
 */
class AppLauncher {

    public static void main(final String[] args) {
        App.main(args);
    }

}