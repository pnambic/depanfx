package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;

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
            p, DepanFxNodeList.class, Collections.emptyMap()));
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

  /**
   * Bind a pop-up to table cell for the resource name.
   */
  public static class NodeListCell<T>
      extends TableCell<T, String> {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final Scene scene;

    private final BiConsumer<
            T, DepanFxWorkspaceResource<DepanFxNodeList>>
        nodeListConsumer;

    public NodeListCell(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        BiConsumer<
                T, DepanFxWorkspaceResource<DepanFxNodeList>>
            nodeListConsumer) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.scene = scene;
      this.nodeListConsumer = nodeListConsumer;
    }

    @Override
    protected void updateItem(String displayName, boolean empty) {
      super.updateItem(displayName, empty);

      if (empty) {
        setGraphic(null);
        setContextMenu(null);
        return;
      }
      setText(displayName);
      setContextMenu(buildContextMenu());
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          DepanFxNodeListTableCommands.SELECT_NODE_LIST,
          e -> runNodeListFinder());
      return builder.build();
    }

    private void runNodeListFinder() {
      DepanFxNodeListChooser
          .runNodeListChooser(workspace, dialogRunner, scene)
          .ifPresent(r -> nodeListConsumer.accept(getTableRow().getItem(), r));
    }
  }
}
