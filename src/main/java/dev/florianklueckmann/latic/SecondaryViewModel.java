package dev.florianklueckmann.latic;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryViewModel {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}
