package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.Scene;

public class DepanFxLayoutsChooser {

  /**
   * Provide an existing link display document.
   *
   * The layout resource is an anonymous object that various layout
   * processes will cast to the required type.
   */
  public static Optional<DepanFxWorkspaceResource<Object>>
      runLayoutFinder(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner, Scene scene,
            DepanFxNodeLayoutRegistry layoutRegistry) {

    DepanFxResourceChooser rsrcChooser =
        new DepanFxResourceChooser(workspace, dialogRunner);

    DepanFxResourcePerspectives.prepareResourceFinder(
        rsrcChooser, DepanFxNodeViewData.NODE_VIEW_TOOL_PATH);

    ObservableList<DepanFxResourceFilter> filters =
        rsrcChooser.getExtensionFilters();

    filters.addAll(layoutRegistry.getOpenFilters(c -> true));
    DepanFxResourceFilter allLayoutsFilter =
        layoutRegistry.buildAllLayoutsFilter(c -> true);
    filters.add(allLayoutsFilter);
    rsrcChooser.setSelectedExtensionFilter(allLayoutsFilter);

    return rsrcChooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, Object.class));
  }
}
