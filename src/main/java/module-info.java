module dev.florianklueckmann {
    requires javafx.controls;
    requires javafx.fxml;

    opens dev.florianklueckmann to javafx.fxml;
    exports dev.florianklueckmann;
}