package com.pnambic.depanfx.perspective.chooser;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import java.util.Optional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;

public class DepanFxResourceChooser {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private String title;

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

    title = "Open Resource";
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setInitialResourceName(String initialResourceName) {
    this.initialResourceName = initialResourceName;
  }

  public void setInitialContainer(DepanFxProjectContainer initialContainer) {
    this.initialContainer = initialContainer;
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

  public Optional<DepanFxWorkspaceMember> showOpenDialog(Scene scene) {
    Dialog<DepanFxResourceChooserDialog> openDialog =
        dialogRunner.createDialogAndParent(DepanFxResourceChooserDialog.class);
    DepanFxResourceChooserDialog chooserCtrl = openDialog.getController();
    chooserCtrl.setWorkspace(workspace);
    chooserCtrl.setExtension(extensionFilters);
    chooserCtrl.setActiveFilter(selectedExtensionFilter.getValue());
    chooserCtrl.setInitialResourceName(initialResourceName);
    chooserCtrl.setInitialContainer(initialContainer);

    openDialog.runDialog(title);
    return chooserCtrl.getSelectedResource();
  }
}
