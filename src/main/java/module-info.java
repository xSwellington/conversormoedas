module dev.swellingtonsoares.conversordemoedas {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires com.google.gson;
    opens dev.swellingtonsoares.conversordemoedas to javafx.fxml;
    exports dev.swellingtonsoares.conversordemoedas;
    exports dev.swellingtonsoares.conversordemoedas.model;
    opens dev.swellingtonsoares.conversordemoedas.model to javafx.fxml;
    exports dev.swellingtonsoares.conversordemoedas.interfaces;
    opens dev.swellingtonsoares.conversordemoedas.interfaces to javafx.fxml;
}