package software.latic.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import software.latic.translation.Translation;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class UpdateHelper {
    private static final UpdateHelper updateHelper = new UpdateHelper();
    public static UpdateHelper getInstance() {
        return updateHelper;
    }

    public void updateCheck() {
        var hasUpdate = "";
        try {
            hasUpdate = updateAvailable();
        } catch (IOException | InterruptedException e) {
            //TODO LOG
        }
        if (!hasUpdate.isEmpty()) {

            ButtonType visitDownload = new ButtonType(Translation.getInstance().getTranslation("download"), ButtonBar.ButtonData.YES);
            ButtonType remindLater = new ButtonType(Translation.getInstance().getTranslation("remindLater"));
            ButtonType skipVersion = new ButtonType(Translation.getInstance().getTranslation("skipVersion"));

            Alert alert = new Alert(Alert.AlertType.NONE, "", skipVersion, visitDownload ,remindLater);
            var alertWindow = alert.getDialogPane().getScene().getWindow();
            alertWindow.getScene().getStylesheets().add("/software/latic/main.css");
            alertWindow.setOnCloseRequest(event -> alert.hide());

            alert.getDialogPane().lookupButton(visitDownload).getStyleClass().addAll("btn-green","font-weight-bolder");
            alert.getDialogPane().lookupButton(remindLater).getStyleClass().addAll("btn-grey");
            alert.getDialogPane().lookupButton(skipVersion).getStyleClass().addAll("btn-grey");

            alert.setTitle(Translation.getInstance().getTranslation("updateAvailable"));
            alert.setHeaderText(Translation.getInstance().getTranslation("updateAvailable"));

            var sb = new StringBuilder()
                    .append(Translation.getInstance().getTranslation("downloadInfo"))
                    .append("\n")
                    .append("Changelog:")
                    .append("\n")
                    .append(hasUpdate);
//            var contentText = StringBuilder String.format("%s \n "
//                    , Translation.getInstance().getTranslation("downloadInfo"));
            alert.setContentText(sb.toString());


            Optional<ButtonType> result = alert.showAndWait();

            result.ifPresent(r -> {
                if (r == visitDownload) {
                    if (Desktop.isDesktopSupported()) {
                        new Thread(() -> {
                            try {
                                Desktop.getDesktop().browse(new URI("https://github.com/florianklueckmann/LATIC/releases/latest"));
                            } catch (IOException | URISyntaxException e1) {
                                e1.printStackTrace();
                            }
                        }, "Browser-UpdateCheck-Thread").start();
                    }
                } else if (r == remindLater) {
                    //TODO Set Reminder Date
                    System.out.println("remindLater");
                } else if (r == skipVersion) {
                    //TODO Set dontRemindMe
                    System.out.println("skipVersion");
                }
            });

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (Desktop.isDesktopSupported()) {
                    new Thread(() -> {
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/florianklueckmann/LATIC/releases/latest"));
                        } catch (IOException | URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }, "Browser-UpdateCheck-Thread").start();
                }
            }
        }
    }
    public String updateAvailable() throws IOException, InterruptedException {

        var appBundle = ResourceBundle.getBundle("software.latic.app");
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/florianklueckmann/LATIC/releases/latest"))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        var json = response.body();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.coercionConfigDefaults();

        Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {
        });

        int latestTag = Integer.parseInt(Arrays.stream(map
                        .getOrDefault("tag_name", "0").toString()
                        .replace("v", "")
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("0"));
        int currentVersion = Integer.parseInt(Arrays.stream(appBundle
                        .getString("version")
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("1"));

        return true ? map.getOrDefault("body", "body").toString() : "";
    }
}
