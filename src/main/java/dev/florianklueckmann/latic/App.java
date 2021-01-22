package dev.florianklueckmann.latic;

import dev.florianklueckmann.latic.Translation.Translation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Level;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    protected static final Level loggingLevel = Level.WARN;
    protected static ResourceBundle bundle = null;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("PrimaryView"), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        try {
            bundle = ResourceBundle.getBundle("dev.florianklueckmann.latic.messages", Translation.getInstance().getLocale());
        }
        catch (MissingResourceException e){
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"), bundle);
//        fxmlLoader.setResources(ResourceBundle.getBundle("messages", Locale.ENGLISH));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
