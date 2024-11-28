package com.pnambic.depanfx.scene;

import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuRegistry;

import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

  public static interface SceneOwner { // extends Closeable {
    void saveSession() throws IOException;

    void closeScene(DepanFxSceneController depanFxSceneController)
        throws IOException;
  }

  private final DepanFxSceneMenuRegistry menuRegistry;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxDialogRunner dialogRunner;

  private final Map<DepanFxSceneViewer, Tab> sceneViewers =
      new LinkedHashMap<>();

  private SceneOwner owner;

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

  public static DepanFxSceneController createDepanScene(
      DepanFxDialogRunner dialogRunner,
      List<DepanFxSceneViewer> initViewers,
      SceneOwner owner)
      throws IOException {

    FxControllerAndView<DepanFxSceneController, Node> root =
        dialogRunner.weaveFxmlView(DepanFxSceneController.class);
    DepanFxSceneController controller = root.getController();
    controller.owner = owner;
    initViewers.forEach(controller::addViewer);

    Scene scene = new Scene((Parent) root.getView().get());
    scene.getStylesheets().add(
        DepanFxSceneController.class.getResource("styles.css").toExternalForm());
    return controller;
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

  public Stream<DepanFxSceneViewer> streamViewers() {
    return sceneViewers.keySet().stream();
  }

  @FXML
  public void initialize() {
    fileNewItem.getItems().addAll(newResourceRegistry.buildNewResourceItems());
    fileOpenResourceItem.setOnAction(this::handleByMenuRegistry);
  }

  @FXML
  public void handleClose() {
    try {
      owner.closeScene(this);
    } catch (IOException errIo) {
      throw new RuntimeException("Unable to shutdown", errIo);
    }
  }

  @FXML
  public void handleSaveSession() {
    try {
      owner.saveSession();
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

  public Scene getScene() {
    return viewRoot.getScene();
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
