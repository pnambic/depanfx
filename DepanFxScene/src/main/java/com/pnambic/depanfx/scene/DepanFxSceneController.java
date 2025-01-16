package com.pnambic.depanfx.scene;

import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuRegistry;

import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

@Component
@FxmlView("scene.fxml")
public class DepanFxSceneController {

  public static interface SceneOwner { // extends Closeable {
    void saveSession() throws IOException;

    void closeScene(DepanFxSceneController depanFxSceneController)
        throws IOException;
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSceneController.class);

  private final DepanFxSceneMenuRegistry menuRegistry;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxDialogRunner dialogRunner;

  private final Map<DepanFxSceneViewer, Tab> sceneViewers =
      new LinkedHashMap<>();

  private SceneOwner owner;

  @FXML
  private TabPane viewRoot;

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

  public void closeScene() {
    // Clear UX resources first (tabs), then map-list of viewers.
    // Leads to DepanFxSceneViewer.closeTab(), which release any resources.
    viewRoot.getTabs().clear();
    sceneViewers.clear();
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
    getSceneTab(viewer).ifPresent(t -> installTab(t, viewer));
  }

  public void removeViewer(DepanFxSceneViewer viewer) {
    sceneViewers.remove(viewer);
    viewer.closeTab();
  }

  private void handleByMenuRegistry(ActionEvent event) {
    menuRegistry.dispatch(event);
  }

  private void installTab(Tab tab, DepanFxSceneViewer viewer) {
    sceneViewers.put(viewer, tab);
    viewRoot.getTabs().add(tab);

    tab.setOnClosed(event -> removeViewer(viewer));
  }

  private Optional<Tab> getSceneTab(DepanFxSceneViewer viewer) {
    try {
      return Optional.of(viewer.getSceneTab(this));
    } catch (Exception errAny) {
      LOG.warn("Unable to build viewer {} due to {}",
          viewer.getClass().getName(), errAny.getMessage());
      LOG.info("Unable to build viewer {}",
          viewer.getClass().getName(), errAny);
    }
    return Optional.empty();
  }
}
