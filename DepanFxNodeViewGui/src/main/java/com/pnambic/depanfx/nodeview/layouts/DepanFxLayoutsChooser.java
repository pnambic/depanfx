package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilterModel;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;

public class DepanFxLayoutsChooser {

  public static final String SELECT_LAYOUT = "Select Layout...";

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

    ObservableList<DepanFxResourceFilterModel> filters =
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

  public static class LayoutControl {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final TextField layoutField;

    private final DepanFxNodeLayoutRegistry layoutRegistry;

    private DepanFxWorkspaceResource<Object> layoutRsrc;

    public LayoutControl(DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        TextField layoutField,
        DepanFxNodeLayoutRegistry layoutRegistry) {

      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.layoutField = layoutField;
      this.layoutRegistry = layoutRegistry;

      layoutField.setContextMenu(buildContextMenu());
    }

    public void runLayoutFinder() {
      DepanFxLayoutsChooser.runLayoutFinder(
          workspace, dialogRunner, layoutField.getScene(), layoutRegistry)
          .ifPresent(this::setLayoutResource);
    }

    public DepanFxWorkspaceResource<Object>
    getLayoutResource() {
      return layoutRsrc;
    }

    public void setLayoutResource(
        DepanFxWorkspaceResource<Object> layoutRsrc) {
      this.layoutRsrc = layoutRsrc;
      layoutField.setText(
          DepanFxProjects.asReferenceLabel(workspace, layoutRsrc));
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          SELECT_LAYOUT,
          e -> runLayoutFinder());
      return builder.build();
    }

  }
}
