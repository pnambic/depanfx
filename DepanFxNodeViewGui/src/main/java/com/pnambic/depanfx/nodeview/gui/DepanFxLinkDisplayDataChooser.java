package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;

import javafx.scene.Scene;

public class DepanFxLinkDisplayDataChooser {

  public static final DepanFxResourceFilter LINK_DISPLAY_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Link Display",
          DepanFxNodeViewLinkDisplayData.NODE_VIEW_LINK_DISPLAY_EXT,
          DepanFxNodeViewLinkDisplayData.class);

  /**
   * Provide an existing link display document.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>>
      runLinkDisplayFinder(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner, Scene scene) {

    DepanFxResourceChooser rsrcChooser =
        prepareChooser(workspace, dialogRunner);
    return rsrcChooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxNodeViewLinkDisplayData.class));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeViewLinkDisplayData.LINK_DISPLAY_PATH);
    result.getExtensionFilters().add(LINK_DISPLAY_FILTER);
    result.setSelectedExtensionFilter(LINK_DISPLAY_FILTER);
    return result;
  }
}
