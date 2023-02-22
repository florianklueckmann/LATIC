package software.latic.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import software.latic.Logging;
import software.latic.translation.Translation;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class UpdateHelper {
    private static final UpdateHelper updateHelper = new UpdateHelper();

    public static UpdateHelper getInstance() {
        return updateHelper;
    }

    private boolean shouldCheckForUpdate(String latestReleaseTag) {
        var skipVersion = Settings.userPreferences.get("skipVersion", "");
        var lastReminder = Settings.userPreferences.get("lastUpdateReminder", LocalDate.now().toString());

        var shouldSkipVersion = !skipVersion.isEmpty() && skipVersion.equals(latestReleaseTag);

        var shouldRemindAgain = LocalDate.parse(lastReminder).until(LocalDate.now()).getDays() >= 14;

        return !shouldSkipVersion && shouldRemindAgain;
    }

    public void updateCheck() {
        var latestReleaseInfo = getLatestReleaseInfo();
        if (latestReleaseInfo.isEmpty()) {
            return;
        }

        var tagName = latestReleaseInfo.getOrDefault("tag_name", "").toString();
        var changeLog = latestReleaseInfo.getOrDefault("body", "");

        if (shouldCheckForUpdate(tagName)) {
            updateAlert(tagName, changeLog);
        } else {
            updateAlert(tagName, changeLog);
        }
    }

    private void updateAlert(String tagName, Object changeLog) {
        ButtonType visitDownload = new ButtonType(Translation.getInstance().getTranslation("download"), ButtonBar.ButtonData.YES);
        ButtonType remindLater = new ButtonType(Translation.getInstance().getTranslation("remindLater"));
        ButtonType skipVersion = new ButtonType(Translation.getInstance().getTranslation("skipVersion"));

        Alert alert = new Alert(Alert.AlertType.NONE, "", skipVersion, visitDownload, remindLater);
        var alertWindow = alert.getDialogPane().getScene().getWindow();
        alertWindow.getScene().getStylesheets().add("/software/latic/main.css");
        alertWindow.setOnCloseRequest(event -> alert.hide());

        alert.getDialogPane().lookupButton(visitDownload).getStyleClass().addAll("btn-green", "font-weight-bolder");
        alert.getDialogPane().lookupButton(remindLater).getStyleClass().addAll("btn-grey");
        alert.getDialogPane().lookupButton(skipVersion).getStyleClass().addAll("btn-grey");

        alert.setHeaderText(Translation.getInstance().getTranslation("downloadInfo") + " - " + tagName);
        alert.setTitle(Translation.getInstance().getTranslation("updateAvailable") + " - " + tagName);
        alert.setContentText(changeLog.toString());

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
                Settings.userPreferences.put("lastUpdateReminder", LocalDate.now().toString());
            } else if (r == skipVersion) {
                Settings.userPreferences.put("skipVersion", tagName);
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

    public Map<String, Object> getLatestReleaseInfo() {

        var appBundle = ResourceBundle.getBundle("software.latic.app");
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/florianklueckmann/LATIC/releases/latest"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response;
        Map<String, Object> map;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Logging.getInstance().warn(e.getClass().getName(), e.getMessage());
            return Map.of();
        }

        var json = response.body();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.coercionConfigDefaults();

        try {
            map = objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            Logging.getInstance().warn(e.getClass().getName(), e.getMessage());
            return Map.of();
        }

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

        var hasUpdate = latestTag > currentVersion;

        return hasUpdate ? map : Map.of();
    }
}
