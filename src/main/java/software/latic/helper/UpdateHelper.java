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
import java.util.*;

public class UpdateHelper {
    private static final UpdateHelper updateHelper = new UpdateHelper();

    public static UpdateHelper getInstance() {
        return updateHelper;
    }


    private String currentVersion = "";

    public String getCurrentVersion() {
        return currentVersion;
    }

    public UpdateHelper() {
        this.currentVersion = System.getProperty("latic.version");
        if (this.currentVersion == null || this.currentVersion.isEmpty()) {
            var appBundle = ResourceBundle.getBundle("software.latic.app");
            this.currentVersion = appBundle.getString("version");
        }
    }

    private boolean shouldCheckForUpdate(String latestReleaseTag) {
        var skipVersion = Settings.userPreferences.get("skipVersion", "");
        var lastReminderStr = Settings.userPreferences.get("lastUpdateReminder", "");

        var shouldSkipVersion = !skipVersion.isEmpty() && skipVersion.equals(latestReleaseTag);

        boolean shouldRemindAgain;
        if (lastReminderStr.isEmpty()) {
            shouldRemindAgain = true;
        } else {
            shouldRemindAgain = LocalDate.parse(lastReminderStr).until(LocalDate.now()).getDays() >= 14;
        }

        return !shouldSkipVersion && shouldRemindAgain;
    }

    public void updateCheck() {
        performUpdateCheck(false);
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

        String headerText = Translation.getInstance().getTranslation("downloadInfo") + " - " + tagName;
        if (isUnstable()) {
            headerText = headerText + "\n\n" + String.format(Translation.getInstance().getTranslation("unstableVersionWarning"), currentVersion);
        }

        alert.setHeaderText(headerText);
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

    public boolean isUnstable() {
        return currentVersion.contains("-");
    }

    public void manualUpdateCheck() {
        performUpdateCheck(true);
    }

    private void performUpdateCheck(boolean manual) {
        var latestReleaseInfo = getLatestReleaseInfo();
        if (latestReleaseInfo.isEmpty()) {
            return;
        }

        var tagName = latestReleaseInfo.getOrDefault("tag_name", "").toString();
        var changeLog = latestReleaseInfo.getOrDefault("body", "");

        if (hasUpdate(tagName)) {
            if (manual || shouldCheckForUpdate(tagName)) {
                updateAlert(tagName, changeLog);
            }
        } else if (manual) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Translation.getInstance().getTranslation("noUpdateAvailable"));
            alert.setHeaderText(null);
            alert.setContentText(Translation.getInstance().getTranslation("upToDate"));
            var alertWindow = alert.getDialogPane().getScene().getWindow();
            alertWindow.getScene().getStylesheets().add("/software/latic/main.css");
            alert.showAndWait();
        }
    }

    private boolean hasUpdate(String latestTagName) {
        int latestTag = parseVersion(latestTagName);
        int currentVersionNum = parseVersion(this.currentVersion);

        return latestTag > currentVersionNum || isUnstable();
    }

    public Map<String, Object> getLatestReleaseInfo() {

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

        return map;
    }

    private int parseVersion(String version) {
        try {
            String cleaned = version.replace("v", "");
            if (cleaned.contains("-")) {
                cleaned = cleaned.substring(0, cleaned.indexOf("-"));
            }
            return Integer.parseInt(Arrays.stream(cleaned.split("\\."))
                    .reduce((s, s2) -> s + s2)
                    .orElse("0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
