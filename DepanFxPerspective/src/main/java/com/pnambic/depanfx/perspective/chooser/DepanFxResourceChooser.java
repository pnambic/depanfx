package com.pnambic.depanfx.perspective.chooser;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DepanFxResourceChooser {

  private DepanFxDialogRunner dialogRunner;

  private String title;

  private DepanFxWorkspace workspace;

  private DepanFxProjectContainer initialContainer;

  private String initialResourceName;

  /////////////////////////////////////
  // Stolen from javafx.stage.FileChooser

  private ObservableList<DepanFxResourceFilter> extensionFilters =
      FXCollections.observableArrayList();

  private ObjectProperty<DepanFxResourceFilter> selectedExtensionFilter =
      new SimpleObjectProperty<>();

  public DepanFxResourceChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Optional<DepanFxWorkspaceMember> showOpenDialog(Scene scene) {
    Dialog<DepanFxResourceChooserDialog> openDialog =
        dialogRunner.createDialogAndParent(DepanFxResourceChooserDialog.class);
    DepanFxResourceChooserDialog chooserCtrl = openDialog.getController();
    chooserCtrl.setWorkspace(workspace);
    chooserCtrl.setExtension(extensionFilters);
    chooserCtrl.setActiveFilter(selectedExtensionFilter.getValue());
    chooserCtrl.setInitialResourceName(initialResourceName);
    chooserCtrl.setInitialContainer(initialContainer);

    openDialog.runDialog("Open Resource");
    return chooserCtrl.getSelectedResource();
  }

  public Optional<DepanFxWorkspaceResource> showOpenDialog(Stage stage) {
    Dialog<DepanFxResourceChooserDialog> openDialog =
        dialogRunner.createDialogAndParent(DepanFxResourceChooserDialog.class);
    openDialog.runDialog(title);
    openDialog.getController();
    return Optional.empty();
  }

  public void setInitialResourceName(String initialResourceName) {
    this.initialResourceName = initialResourceName;
  }

  public void setInitialContainer(String initialResourceName) {
    this.initialResourceName = initialResourceName;
  }

  public void setActiveFilter(DepanFxResourceFilter activeFilter) {
    selectedExtensionFilter.set(activeFilter);
  }

  public ObservableList<DepanFxResourceFilter> getExtensionFilters() {
    return extensionFilters;
  }

  public void setExtensionFilters(
      ObservableList<DepanFxResourceFilter> extensionFilters) {
    this.extensionFilters = extensionFilters;
  }

  public void setSelectedExtensionFilter(
      DepanFxResourceFilter resourceFilter) {
    selectedExtensionFilter.set(resourceFilter);
  }
}
