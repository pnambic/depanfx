package com.pnambic.depanfx.scene;

import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneViewPanelRegistry;

import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.Window;

@DepanFxFxmlDialog
@FxmlView("scene.fxml")
public class DepanFxSceneController {

  public static interface SceneOwner { // extends Closeable {
    void saveSession() throws IOException;

    void closeScene(DepanFxSceneService sceneSrvc)
        throws IOException;

    boolean isStage(Stage sessionStage);
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSceneController.class);

  private final DepanFxSceneMenuRegistry menuRegistry;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxSceneViewPanelRegistry viewPanelRegistry;

  private final DepanFxSceneService sceneSrvc;

  // Preserves order of tabs for serialization
  private final Map<Tab, DepanFxSceneViewer> sceneTabs=
      new LinkedHashMap<>();

  private SceneOwner owner;

  @FXML
  private TabPane viewRoot;

  // Internal menus
  @FXML
  private Menu fileNewItem;

  @FXML
  private Menu viewPanelsItem;

  public static DepanFxSceneController createDepanScene(
      DepanFxDialogRunner dialogRunner,
      SceneOwner owner)
      throws IOException {

    FxControllerAndView<DepanFxSceneController, Node> root =
        dialogRunner.weaveFxmlView(DepanFxSceneController.class);
    DepanFxSceneController controller = root.getController();
    controller.owner = owner;

    Scene scene = new Scene((Parent) root.getView().get());
    scene.getStylesheets().add(
        DepanFxSceneController.class.getResource("styles.css").toExternalForm());
    return controller;
  }

  @Autowired
  public DepanFxSceneController(
      DepanFxDialogRunner dialogRunner,
      DepanFxSceneMenuRegistry menuRegistry,
      DepanFxNewResourceRegistry newResourceRegistry,
      DepanFxSceneViewPanelRegistry viewPanelRegistry) {
    this.menuRegistry = menuRegistry;
    this.newResourceRegistry = newResourceRegistry;
    this.viewPanelRegistry = viewPanelRegistry;

    this.sceneSrvc = new SceneService(dialogRunner);
  }

  @FXML
  public void initialize() {
    fileNewItem.getItems().addAll(newResourceRegistry.buildNewResourceItems());
    viewPanelsItem.getItems().addAll(
        viewPanelRegistry.buildViewPanelItems(sceneSrvc));
  }

  public void closeScene() {
    LOG.info("Close scene invoked");
    // Clear UX resources first (tabs), then map-list of viewers.
    // Leads to DepanFxSceneViewer.closeTab(), which release any resources.
    sceneSrvc.streamViewers().forEach(v ->v.closeTab());
    viewRoot.getTabs().clear();
    sceneTabs.clear();
  }

  public Scene getScene() {
    return viewRoot.getScene();
  }

  public void addViewer(DepanFxSceneViewer viewer) {
    getSceneTab(viewer).ifPresent(t -> installTab(t, viewer));
  }

  public void removeViewer(Tab tab) {
    DepanFxSceneViewer viewer = sceneTabs.get(tab);
    viewer.closeTab();
    sceneTabs.remove(tab);
  }

  public DepanFxSceneService getSceneService() {
    return sceneSrvc;
  }

  @FXML
  private void onMenuShowing(Event event) {
    Menu menu = (Menu) event.getSource();
    menu.getItems().stream().forEach(i -> prepareItem(i));
  }

  @FXML
  private void handleFileExit() {
    // Don't try to second guess JavaFX on what needs to happen on an exit.
    // Prolly not on the correct thread anyway.
    //
    // Just tell JavaFX to release the resources.
    // It will call DepanFxApp.close() in the right context,
    // and all the windows will release resources correctly.
    Platform.exit();
  }

  @FXML
  private void handleSaveSession() {
    try {
      owner.saveSession();
    } catch (IOException errIo) {
      throw new RuntimeException("Unable to shutdown", errIo);
    }
  }

  @FXML
  public void handleByRegistry(ActionEvent event) {
    handleByMenuRegistry(event);
  }

  @FXML
  private void handleWelcome() {
    sceneSrvc.getDialogRunner().runDialog(
        DepanFxWelcomeDialog.class, "Welcome To DepanFX");
  }

  @FXML
  private void handleAbout() {
    sceneSrvc.getDialogRunner().runDialog(
        DepanFxAboutDialog.class, "About DepanFX");
  }

  private void prepareItem(MenuItem item) {
    if (item.equals(fileNewItem)) {
      return;
    }
    if (item.equals(viewPanelsItem)) {
      return;
    }
    String menuItemKey = item.getId();
    if (menuItemKey != null) {
      boolean active = menuRegistry.isMenuItemActive(sceneSrvc, menuItemKey);
      item.setDisable(! active);
    }
  }

  private void handleByMenuRegistry(ActionEvent event) {
    menuRegistry.dispatch(sceneSrvc, event);
  }

  private void installTab(Tab tab, DepanFxSceneViewer viewer) {
    sceneTabs.put(tab, viewer);
    viewRoot.getTabs().add(tab);

    tab.setOnClosed(event -> removeViewer(tab));
  }

  private Optional<Tab> getSceneTab(DepanFxSceneViewer viewer) {
    try {
      return Optional.of(viewer.getSceneTab(sceneSrvc));
    } catch (Exception errAny) {
      LOG.warn("Unable to build viewer {} due to {}",
          viewer.getClass().getName(), errAny.getMessage());
      LOG.info("Unable to build viewer {}",
          viewer.getClass().getName(), errAny);
    }
    return Optional.empty();
  }

  /**
   * Encapsulate access to the containing scene
   * and allow access to a limited set of scene capabilities.
   */
  private class SceneService implements DepanFxSceneService {

    private final DepanFxDialogRunner dialogRunner;

    public SceneService(DepanFxDialogRunner dialogRunner) {
      this.dialogRunner = dialogRunner;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DepanFxSceneViewer> Optional<T> getViewer(
        Class<T> viewerClass) {
      Tab activeTab = viewRoot.getSelectionModel().getSelectedItem();
      if (activeTab != null) {
        DepanFxSceneViewer result = sceneTabs.get(activeTab);
        if (viewerClass.isAssignableFrom(result.getClass())) {
          return Optional.of((T) result);
        }
      }
      return Optional.empty();
    }

    @Override
    public DepanFxDialogRunner getDialogRunner() {
      return dialogRunner;
    }

    @Override
    public void addViewer(DepanFxSceneViewer viewer) {
      DepanFxSceneController.this.addViewer(viewer);
    }

    @Override
    public void closeScene() {
      DepanFxSceneController.this.closeScene();
    }

    @Override
    public String getLabel() {
      if (getScene().getWindow() instanceof Stage stage) {
        return stage.getTitle();
      }
      return "DepanFX";
    }

    @Override
    public String getDescription() {
      return "DepanFX scene.";
    }

    @Override
    public Rectangle2D getDisplayRectangle() {
      Window window = getScene().getWindow();
      return new Rectangle2D(
          window.getX(), window.getY(),
          window.getWidth(), window.getHeight());
    }

    @Override
    public Stream<DepanFxSceneViewer> streamViewers() {
      return sceneTabs.values().stream();
    }

    @Override
    public boolean isStage(Stage sessionStage) {
      return owner.isStage(sessionStage);
    }
  }
}
