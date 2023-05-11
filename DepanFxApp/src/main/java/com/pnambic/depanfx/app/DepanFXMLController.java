package com.pnambic.depanfx.app;

import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.gui.DepanFxProjectListViewer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class DepanFXMLController {

  public static final String WORKSPACE_TAB = "Workspace";

  private final DepanFxWorkspace workspace;

  public DepanFXMLController(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @FXML
  private TabPane viewRoot;

  @FXML
  private Label welcomeLabel;

  @FXML
  private MenuItem fileOpenProjectItem;

  @FXML
  private MenuItem fileExitItem;

  public void initialize() {
    DepanFxProjectListViewer workspaceViewer = new DepanFxProjectListViewer(workspace);
    Tab workspaceTab = workspaceViewer.createWorkspaceTab(WORKSPACE_TAB);
    viewRoot.getTabs().add(workspaceTab);

    String javaVersion = System.getProperty("java.version");
    String javafxVersion = System.getProperty("javafx.version");
    welcomeLabel.setText("* Welcome to DepanFX *"
        + "\nBuilt with JavaFX " + javafxVersion
        + "\nRunning on Java " + javaVersion + ".");

    fileOpenProjectItem.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        workspaceViewer.openProject();
      }
    });

    fileExitItem.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        workspace.exit();
        Platform.exit();
      }
    });
  }
}
