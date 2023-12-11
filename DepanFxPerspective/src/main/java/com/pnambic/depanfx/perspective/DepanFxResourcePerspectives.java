package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import javafx.scene.control.Cell;
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
}
