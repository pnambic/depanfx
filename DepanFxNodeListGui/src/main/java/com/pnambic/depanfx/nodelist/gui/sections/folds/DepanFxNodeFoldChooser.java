/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodelist.gui.sections.folds;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Optional;
import java.util.function.BiConsumer;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;

public class DepanFxNodeFoldChooser {

  public static final String SELECT_NODE_FOLDING = "Select Node Folding...";

  public static final DepanFxResourceFilter NODE_FOLDING_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Link Folding",
          DepanFxNodeFoldData.NODE_FOLD_TOOL_EXT,
          DepanFxNodeFoldData.class);

  /**
   * Bind a pop-up to text field for the resource name.
   */
  public static class NodeFoldingControl {

    public final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private DepanFxWorkspaceResource<DepanFxNodeFoldData>
        nodeFoldingRsrc;

    private final TextField nodeFoldingField;

    public NodeFoldingControl(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        TextField nodeFoldingField) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.nodeFoldingField = nodeFoldingField;
      nodeFoldingField.setContextMenu(buildContextMenu());
    }

    public void setNodeFoldResource(
        DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldingRsrc) {
      this.nodeFoldingRsrc = nodeFoldingRsrc;
      nodeFoldingField.setText(
          DepanFxProjects.asSaveLabel(workspace, nodeFoldingRsrc));
    }

    public DepanFxWorkspaceResource<DepanFxNodeFoldData>
        getNodeFoldingResource() {

      return nodeFoldingRsrc;
    }

    public void runNodeFoldingFinder() {
      DepanFxNodeFoldChooser
          .runNodeFoldingFinder(
                workspace, dialogRunner, nodeFoldingField.getScene())
          .ifPresent(this::setNodeFoldResource);
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          SELECT_NODE_FOLDING, e -> runNodeFoldingFinder());
      return builder.build();
    }
  }

  /**
   * Provide an existing link matcher.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  runNodeFoldingFinder(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      Scene scene) {

    DepanFxResourceChooser chooser = prepareChooser(workspace, dialogRunner);
    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxNodeFoldData.class));
  }

  /**
   * Bind a pop-up to table cell for the resource name.
   */
  public static class NodeFoldingCell<T>
      extends TableCell<T, String> {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final Scene scene;

    private final BiConsumer<
            TableRow<T>,
            DepanFxWorkspaceResource<DepanFxNodeFoldData>> foldConsumer;

    public NodeFoldingCell(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        BiConsumer<
        TableRow<T>,
            DepanFxWorkspaceResource<DepanFxNodeFoldData>> foldConsumer) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.scene = scene;
      this.foldConsumer = foldConsumer;
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
          SELECT_NODE_FOLDING, e -> runNodeFolderFinder());
      return builder.build();
    }

    private void runNodeFolderFinder() {
      DepanFxNodeFoldChooser
          .runNodeFoldingFinder(workspace, dialogRunner, scene)
          .ifPresent(r -> foldConsumer.accept(getTableRow(), r));
    }
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxProjects.TOOLS_PATH);
    result.getExtensionFilters().add(NODE_FOLDING_FILTER);
    result.setSelectedExtensionFilter(NODE_FOLDING_FILTER);
    return result;
  }
}
