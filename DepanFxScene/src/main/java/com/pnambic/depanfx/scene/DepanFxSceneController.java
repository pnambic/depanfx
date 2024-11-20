package com.pnambic.depanfx.scene;

import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuRegistry;

import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;

@Component
@FxmlView("scene.fxml")
public class DepanFxSceneController {

  private final DepanFxSceneMenuRegistry menuRegistry;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxDialogRunner dialogRunner;

  private final Map<DepanFxSceneViewer, Tab> sceneViewers = new HashMap<>();

  private Closeable onClose;

  @FXML
  private TabPane viewRoot;

  @FXML
  private Label welcomeLabel;

  @FXML
  private ImageView welcomeImage;

  @FXML
  private Menu fileNewItem;

  @FXML
  private MenuItem fileOpenResourceItem;

  public static Scene createDepanScene(
      FxWeaver fxWeaver,
      List<DepanFxSceneViewer> initViewers,
      Closeable onClose)
      throws IOException {

    FxControllerAndView<DepanFxSceneController, Node> root =
       fxWeaver.load(DepanFxSceneController.class);
    root.getController().onClose = onClose;
    initViewers.forEach(root.getController()::addViewer);

    Scene scene = new Scene((Parent) root.getView().get());
    scene.getStylesheets().add(
        DepanFxSceneController.class.getResource("styles.css").toExternalForm());
    return scene;
  }

  @Autowired
  public DepanFxSceneController(
      DepanFxSceneMenuRegistry menuRegistry,
      DepanFxNewResourceRegistry newResourceRegistry,
      DepanFxDialogRunner dialogRunner) {
    this.menuRegistry = menuRegistry;
    this.newResourceRegistry = newResourceRegistry;
    this.dialogRunner = dialogRunner;
  }

  @FXML
  public void initialize() {
    fileNewItem.getItems().addAll(newResourceRegistry.buildNewResourceItems());
    fileOpenResourceItem.setOnAction(this::handleByMenuRegistry);
  }

  @FXML
  public void handleClose() {
    try {
      onClose.close();
    } catch (IOException errIo) {
      throw new RuntimeException("Unable to shutdown", errIo);
    }
  }

  @FXML
  public void handleByRegistry(ActionEvent event) {
    menuRegistry.dispatch(event);
  }

  @FXML
  public void handleImportItem(ActionEvent event) {
    menuRegistry.dispatch(event);
  }

  @FXML
  public void handleWelcome() {
    dialogRunner.runDialog(DepanFxWelcomeDialog.class, "Welcome To DepanFX");
  }

  @FXML
  public void handleAbout() {
    dialogRunner.runDialog(DepanFxAboutDialog.class, "About DepanFX");
  }

  public void addViewer(DepanFxSceneViewer viewer) {
    Tab tab = viewer.getSceneTab(this);
    sceneViewers.put(viewer, tab);

    viewRoot.getTabs().add(tab);
  }

  private void handleByMenuRegistry(ActionEvent event) {
    menuRegistry.dispatch(event);
  }
}
