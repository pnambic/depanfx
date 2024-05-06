package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Optional;

import javafx.scene.Scene;

public class DepanFxNodeListChooser {

  private static final DepanFxResourceFilter NODE_LIST_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Node List", DepanFxNodeList.NODE_LIST_EXT, DepanFxNodeList.class);

  /**
   * Obtain an existing node list with a file chooser.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxNodeList>>
      runNodeListChooser(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner,
            Scene scene) {

    DepanFxResourceChooser chooser = prepareChooser(workspace, dialogRunner);
    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxNodeList.class));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxProjects.TOOLS_PATH);
    result.getExtensionFilters().add(NODE_LIST_RSRC_FILTER);
    result.setSelectedExtensionFilter(NODE_LIST_RSRC_FILTER);
    return result;
  }
}
