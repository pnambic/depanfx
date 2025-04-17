/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeFilterSequenceData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;
import java.util.function.BiConsumer;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class DepanFxNodeFiltersSequenceChooser {

  public static final String NODE_FILTER_SEQUENCE =
      "Node Filter Sequence";

  public static final String SELECT_NODE_FILTER_SEQUENCE =
      "Select Node Filter Sequence...";

  public static final String FILTER_SEQUENCE_TOOL_EXT =
      DepanFxNodeFilterSequenceData.NODE_FILTER_SEQUENCE_TOOL_EXT;

  public static final DepanFxResourceFilter NODE_FILTER_SEQUENCE_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          NODE_FILTER_SEQUENCE, FILTER_SEQUENCE_TOOL_EXT,
          DepanFxNodeFilterSequenceData.class);

  /**
   * Bind a pop-up to text field for the resource name.
   */
  public static class NodeFilterControl {

    public final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry;

    private final TextField nodeFilterField;

    private DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
        nodeFilterRsrc;

    public NodeFilterControl(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry,
        TextField nodeFilterField) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.nodeFiltersDialogRegistry = nodeFiltersDialogRegistry;
      this.nodeFilterField = nodeFilterField;
      nodeFilterField.setContextMenu(buildContextMenu());
    }

    public void setNodeFilterRsrc(
        DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> nodeFilterRsrc) {
      this.nodeFilterRsrc = nodeFilterRsrc;
      nodeFilterField.setText(getNodeFilterRsrcName());
    }

    public DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
        getNodeFilterResource() {

      return nodeFilterRsrc;
    }

    public String getNodeFilterRsrcName() {
      if (nodeFilterRsrc != null) {
        return nodeFilterRsrc.getDocument().getMemberPath().toString();
      }
      // Let the text input field show a prompt text.
      return null;
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          SELECT_NODE_FILTER_SEQUENCE, e -> runNodeFilterFinder());
      return builder.build();
    }

    private void runNodeFilterFinder() {
      DepanFxNodeFiltersSequenceChooser
          .runNodeFiltersFinder(
                workspace, dialogRunner, nodeFilterField.getScene(), nodeFiltersDialogRegistry)
          .ifPresent(this::setNodeFilterRsrc);
    }
  }

  /**
   * Bind a pop-up to table cell for the resource name.
   */
  public static class NodeFilterCell<T>
      extends TableCell<T, String> {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final Scene scene;

    private final DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry;

    private final BiConsumer<
            T, DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>> filterConsumer;

    public NodeFilterCell(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry,
        BiConsumer<
            T,
            DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>> filterConsumer) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.scene = scene;
      this.nodeFiltersDialogRegistry = nodeFiltersDialogRegistry;
      this.filterConsumer = filterConsumer;
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
          SELECT_NODE_FILTER_SEQUENCE, e -> runNodeFilterFinder());
      return builder.build();
    }

    private void runNodeFilterFinder() {
      DepanFxNodeFiltersSequenceChooser
          .runNodeFiltersFinder(workspace, dialogRunner, scene, nodeFiltersDialogRegistry)
          .ifPresent(r -> filterConsumer.accept(getTableRow().getItem(), r));
    }
  }

  /**
   * Provide an existing node filter.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>>
      runNodeFiltersFinder(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner,
            Scene scene,
            DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {

    DepanFxResourceChooser chooser = prepareChooser(workspace, dialogRunner);
    DepanFxResourceFilter rsrcFilter =
        DepanFxNodeFilterSequenceToolDialog.NODE_FILTER_SEQUENCE_RSRC_FILTER;
    chooser.getExtensionFilters().add(rsrcFilter);
    chooser.setSelectedExtensionFilter(rsrcFilter);

    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxNodeFilterSequenceData.class));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxBaseFilterData.NODE_FILTERS_TOOL_PATH);
    return result;
  }
}
