module dev.florianklueckmann {
    requires javafx.controls;
    requires javafx.fxml;
    requires stanford.corenlp;


    opens dev.florianklueckmann to javafx.fxml;
    exports dev.florianklueckmann;
}
