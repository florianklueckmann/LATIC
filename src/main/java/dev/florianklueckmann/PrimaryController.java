package dev.florianklueckmann;

import java.io.IOException;
import javafx.fxml.FXML;

import edu.stanford.nlp.io.IOUtils;

import edu.stanford.nlp.simple.*;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void calculateStuff() throws IOException {
        System.out.println("Hi");
    }

}
