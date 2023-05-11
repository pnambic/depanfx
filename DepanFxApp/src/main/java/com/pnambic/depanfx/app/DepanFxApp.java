package com.pnambic.depanfx.app;

import java.io.IOException;

import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DepanFxApp extends Application {

    public static final String WORKSPACE_NAME = "Depan Workspace";

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
        DepanFxWorkspace workspace = DepanFxWorkspaceFactory.createDepanFxWorkspace(WORKSPACE_NAME);
        FXMLLoader result = new FXMLLoader();
        result.setController(new DepanFXMLController(workspace));
        result.setLocation(getClass().getResource("scene.fxml"));
        return (Parent) result.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
