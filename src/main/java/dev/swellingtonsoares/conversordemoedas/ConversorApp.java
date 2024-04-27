package dev.swellingtonsoares.conversordemoedas;

import dev.swellingtonsoares.conversordemoedas.model.HistoricManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ConversorApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ConversorApp.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Conversor de Moedas v1.0");
        stage.setScene(scene);
        stage.setResizable(false);
        scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            try {
                HistoricManager.getInstance().save();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}