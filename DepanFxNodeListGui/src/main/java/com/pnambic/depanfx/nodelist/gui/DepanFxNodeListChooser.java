package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.io.File;
import java.util.Optional;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class DepanFxNodeListChooser {
  
  private static final ExtensionFilter NODE_LIST_FILTER = 
      DepanFxSceneControls.buildExtFilter(
          "Node List", DepanFxNodeList.NODE_LIST_EXT);

  /**
   * Obtain an existing git repo tooldata with file chooser.
   * @param window 
   * @param targetType 
   */
  public static Optional<DepanFxWorkspaceResource> runNodeListFinder(
      DepanFxWorkspace workspace, Window window) {
    FileChooser fileChooser = prepareNodeListFinder(workspace);
    File selectedFile = fileChooser.showOpenDialog(window);
    if (selectedFile != null) {
      return workspace
          .toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> DepanFxWorkspaceFactory.loadDocument(
              workspace, p, DepanFxNodeList.class));
    }
    return Optional.empty();
  }

  private static FileChooser prepareNodeListFinder(DepanFxWorkspace workspace) {
    FileChooser result = DepanFxResourcePerspectives.prepareToolFinder(
        workspace, DepanFxProjects.ANALYSES_PATH);
    setNodeListFilters(result);
    return result;
  }

  public static void setNodeListFilters(FileChooser result) {
    result.getExtensionFilters().add(NODE_LIST_FILTER);
    result.setSelectedExtensionFilter(NODE_LIST_FILTER);
  }
}
