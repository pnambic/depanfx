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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

@DepanFxFxmlDialog
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

  private final DepanFxSceneViewPanelRegistry viewPanelRegistry;

  private final DepanFxDialogRunner dialogRunner;

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

  private DepanFxSceneService sceneSrvc;

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
      DepanFxSceneViewPanelRegistry viewPanelRegistry,
      DepanFxDialogRunner dialogRunner) {
    this.menuRegistry = menuRegistry;
    this.newResourceRegistry = newResourceRegistry;
    this.viewPanelRegistry = viewPanelRegistry;
    this.dialogRunner = dialogRunner;

    this.sceneSrvc = new SceneService();
  }

  public Stream<DepanFxSceneViewer> streamViewers() {
    return sceneTabs.values().stream();
  }

  @FXML
  public void initialize() {
    fileNewItem.getItems().addAll(newResourceRegistry.buildNewResourceItems());
    viewPanelsItem.getItems().addAll(
        viewPanelRegistry.buildViewPanelItems(this));
  }

  public void closeScene() {
    // Clear UX resources first (tabs), then map-list of viewers.
    // Leads to DepanFxSceneViewer.closeTab(), which release any resources.
    LOG.info("Close scene invoked");
    viewRoot.getTabs().clear();
    sceneTabs.clear();
  }

  @FXML
  public void onMenuShowing(Event event) {
    Menu menu = (Menu) event.getSource();
    menu.getItems().stream().forEach(i -> prepareItem(i));
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
    handleByMenuRegistry(event);
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

  public void removeViewer(Tab tab) {
    DepanFxSceneViewer viewer = sceneTabs.get(tab);
    viewer.closeTab();
    sceneTabs.remove(tab);
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
      return Optional.of(viewer.getSceneTab(this));
    } catch (Exception errAny) {
      LOG.warn("Unable to build viewer {} due to {}",
          viewer.getClass().getName(), errAny.getMessage());
      LOG.info("Unable to build viewer {}",
          viewer.getClass().getName(), errAny);
    }
    return Optional.empty();
  }

  private class SceneService implements DepanFxSceneService {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DepanFxSceneViewer> Optional<T> getViewer(
        Class<T> viewerClass) {
      Tab activeTab = viewRoot.getSelectionModel().getSelectedItem();
      DepanFxSceneViewer result = sceneTabs.get(activeTab);
      if (viewerClass.isAssignableFrom(result.getClass())) {
        return Optional.of((T) result);
      }
      return Optional.empty();
    }
  }
}
