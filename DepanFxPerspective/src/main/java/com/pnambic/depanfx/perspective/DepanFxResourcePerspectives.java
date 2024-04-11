package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Cell;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

/**
 * Utilities for defining resource perspectives
 */
public class DepanFxResourcePerspectives {

  private DepanFxResourcePerspectives() {
    // Prevent instantiation.
  }

  public static void installOnOpen(
      Cell<DepanFxWorkspaceMember> cell,
      Path docPath,
      Consumer<Path> onOpenPath) {
    cell.setOnMouseClicked(
        e -> DepanFxSceneControls.handleDoubleClickOpenPath(
            e, docPath, onOpenPath));
  }

  public static URI toUri(TextField text) {
    // TODO: reasonableness and safety checks.
    return Path.of(text.getText()).toUri();
  }

  public static Optional<DepanFxProjectDocument> toProjecDocument(
      DepanFxWorkspace workspace, TextField text) {
    return workspace.toProjectDocument(toUri(text));
  }

  public static <T> Optional<DepanFxWorkspaceResource<T>> toResource(
      DepanFxWorkspace workspace, TextField text, Class<T> rsrcType) {
    return toProjecDocument( workspace, text)
        .flatMap(p -> workspace .getWorkspaceResource(p, rsrcType));
  }

  public static <
          Dlg extends DepanFxBaseToolDialog<Data>,
          Data extends DepanFxBaseToolData>
    Dialog<Dlg> runEditDialog(
        DepanFxProjectDocument projDoc,
        Data toolData,
        DepanFxDialogRunner dialogRunner,
        Class<Dlg> dialogType,
        String title) {
    Dialog<Dlg> dlg = dialogRunner.createDialogAndParent(dialogType);
    dlg.getController().setDestination(projDoc);
    dlg.getController().setTooldata(toolData);
    dlg.runDialog(title);
    return dlg;
  }

  public static <
        Dlg extends DepanFxBaseToolDialog<Data>,
        Data extends DepanFxBaseToolData>
  Dialog<Dlg> runCreateDialog(
      Data toolData,
      DepanFxDialogRunner dialogRunner,
      Class<Dlg> dialogType,
      String title) {
    Dialog<Dlg> dlg = dialogRunner.createDialogAndParent(dialogType);
    dlg.getController().setTooldata(toolData);
    dlg.runDialog(title);
    return dlg;
  }

  public static void prepareResourceFinder(
      DepanFxResourceChooser chooser, Path targetPath) {

    DepanFxWorkspace workspace = chooser.getWorkspace();
    Path initPath = DepanFxWorkspaceFactory.bestDocumentPath(
        "temp", workspace, targetPath,
        DepanFxProjects.getCurrentToolsPath(workspace).orElse(null));

    chooser.setInitialResourceName(initPath.getFileName().toString());
    chooser.setInitialContainer(initPath.getParent().toString());
  }

  public static FileChooser prepareToolFinder(
      DepanFxWorkspace workspace, Path targetPath) {

    File initFile = DepanFxWorkspaceFactory.bestDocumentFile(
        "temp", workspace, targetPath,
        DepanFxProjects.getCurrentTools(workspace));

    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(initFile);
    result.setInitialFileName("");
    return result;
  }

  public static Optional<DepanFxProjectDocument> toProjDoc(
      DepanFxWorkspace workspace, TextField destinationField) {

    File dstFile = new File(destinationField.getText());
    return workspace.toProjectDocument(dstFile.toURI());
  }

  public static boolean errorAlert(
      DepanFxProctor validator, String alertHeader) {
    if (validator.hasErrors()) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setContentText(alertHeader);
      alert.setTitle(validator.getSummaryText());
      alert.setHeaderText(validator.getDetailText());
      alert.showAndWait();
      return true;
    }
    return false;
  }
}
