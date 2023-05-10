package com.pnambic.depanfx.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DepanFxApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = createDepanRoot();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("DepanFX w/ JavaFX and Gradle");
        stage.setScene(scene);
        stage.show();
    }

    private Parent createDepanRoot() throws IOException {
        FXMLLoader result = new FXMLLoader();
        result.setController(new DepanFXMLController());
        result.setLocation(getClass().getResource("scene.fxml"));
        return (Parent) result.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
