package com.pnambic.depanfx.scene;

import java.io.Closeable;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterRegistry;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("scene.fxml")
public class DepanFxSceneController {

  public static final String WORKSPACE_TAB = "Workspace";

  private final DepanFxSceneMenuRegistry menuRegistry;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxSceneStarterRegistry starterRegistry;

  private Closeable onClose;

  @FXML
  private TabPane viewRoot;

  @FXML
  private Label welcomeLabel;

  @FXML
  private MenuItem fileOpenProjectItem;

  @FXML
  private MenuItem fileImportItem;

  @FXML
  private Menu fileNewItem;

  @FXML
  private MenuItem fileExitItem;

  public static Scene createDepanScene(FxWeaver fxWeaver, Closeable onClose) throws IOException {

    FxControllerAndView<DepanFxSceneController, Node> root =
       fxWeaver.load(DepanFxSceneController.class);
    root.getController().onClose = onClose;

    Scene scene = new Scene((Parent) root.getView().get());
    scene.getStylesheets().add(
        DepanFxSceneController.class.getResource("styles.css").toExternalForm());
    return scene;
  }

  @Autowired
  public DepanFxSceneController(
      DepanFxSceneMenuRegistry menuRegistry,
      DepanFxNewResourceRegistry newResourceRegistry,
      DepanFxSceneStarterRegistry starterRegistry) {
    this.menuRegistry = menuRegistry;
    this.newResourceRegistry = newResourceRegistry;
    this.starterRegistry = starterRegistry;
  }

  @FXML
  public void initialize() {

    // Tweak welcome tab
    String javaVersion = System.getProperty("java.version");
    String javafxVersion = System.getProperty("javafx.version");
    welcomeLabel.setText("* Welcome to DepanFX *"
        + "\nBuilt with JavaFX " + javafxVersion
        + "\nRunning on Java " + javaVersion + ".");

    // Start any initial tabs
    starterRegistry.addStarterTabs(viewRoot);
    fileNewItem.getItems().addAll(newResourceRegistry.buildNewResourceItems());

    fileImportItem.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        menuRegistry.dispatch(event);
      }
    });

    fileExitItem.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        try {
          onClose.close();
        } catch (IOException errIo) {
          throw new RuntimeException("Unable to shutdown", errIo);
        }
      }
    });
  }
}
