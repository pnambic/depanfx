package com.pnambic.depanfx.nodelist.gui.link;

import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
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

public class DepanFxLinkMatcherChooser {

  public static final DepanFxResourceFilter LINK_MATCHER_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Link Matcher",
          DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_EXT,
          DepanFxLinkMatcherDocument.class);

  /**
   * Provide an existing link matcher.
   */
  public static Optional<DepanFxWorkspaceResource> runLinkMatcherFinder(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner, Scene scene) {
    DepanFxResourceChooser chooser = prepareChooser(workspace, dialogRunner);
    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxLinkMatcherDocument.class));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxProjects.TOOLS_PATH);
    result.getExtensionFilters().add(LINK_MATCHER_FILTER);
    result.setSelectedExtensionFilter(LINK_MATCHER_FILTER);
    return result;
  }
}
