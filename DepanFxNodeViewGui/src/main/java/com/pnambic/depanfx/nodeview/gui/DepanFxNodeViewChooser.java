package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;

import javafx.scene.Scene;

public class DepanFxNodeViewChooser {

  /**
   * Provide an existing link display document.
   *
   * The layout resource is an anonymous object that various layout
   * processes will cast to the required type.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxNodeViewData>>
      runChooser(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner, Scene scene) {

    DepanFxResourceChooser rsrcChooser =
        new DepanFxResourceChooser(workspace, dialogRunner);

    DepanFxResourcePerspectives.prepareResourceFinder(
        rsrcChooser, DepanFxNodeViewData.NODE_VIEW_TOOL_PATH);

    rsrcChooser.getExtensionFilters().add(
        DepanFxNodeViews.NODE_VIEW_RSRC_FILTER);
    rsrcChooser.setSelectedExtensionFilter(
        DepanFxNodeViews.NODE_VIEW_RSRC_FILTER);

    return rsrcChooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxNodeViewData.class));
  }
}
