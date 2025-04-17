package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;

import javafx.scene.Scene;

public class DepanFxLinkMatcherSequenceChooser {

  /**
   * Provide an existing link display document.
   *
   * The layout resource is an anonymous object that various layout
   * processes will cast to the required type.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>>
      runChooser(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner, Scene scene) {

    DepanFxResourceChooser rsrcChooser =
        new DepanFxResourceChooser(workspace, dialogRunner);

    DepanFxResourcePerspectives.prepareResourceFinder(
        rsrcChooser, DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_PATH);

    rsrcChooser.getExtensionFilters().add(
        DepanFxLinkMatcherSequenceToolDialog.LINK_MATCHER_SEQUENCE_RSRC_FILTER);
    rsrcChooser.setSelectedExtensionFilter(
        DepanFxLinkMatcherSequenceToolDialog.LINK_MATCHER_SEQUENCE_RSRC_FILTER);

    return rsrcChooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxLinkMatcherSequenceDocument.class));
  }
}
