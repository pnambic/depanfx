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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewMerger;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.scene.DepanFxActionTableCell;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("node-views-merge-dialog.fxml")
public class DepanFxNodeViewsMergeDialog
    extends DepanFxBaseToolDialog<DepanFxNodeViewData> {

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(
      DepanFxNodeViewsMergeDialog.class);

  public static final String MERGE_NODE_VIEWS = "Merge Node Views";

  private static final String EXT = DepanFxNodeViewData.NODE_VIEW_TOOL_EXT;

  private static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Node View", EXT);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxNodeViewData>> nodeViewsTable;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxNodeViewsMergeDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxNodeViewData.class);
    this.dialogRunner = dialogRunner;
  }

  public static DepanFxNodeViewsMergeDialog runMergeDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {

    Dialog<DepanFxNodeViewsMergeDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxNodeViewsMergeDialog.class);
    DepanFxNodeViewsMergeDialog result = dlg.getController();
    result.clearToolResource();

    dlg.runDialog(MERGE_NODE_VIEWS);
    return result;
  }

  @FXML
  public void initialize() {
    nodeViewsTable.setContextMenu(buildNodeViewsMergeTableMenu());

    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxNodeViewData>>
    columnBinder = new DepanFxTableColumnBinder<>(nodeViewsTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeViewData>, String>
    rsrcColumn = columnBinder.next();
    rsrcColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getDocument().getMemberName()));

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeViewData>, String>
    labelColumn = columnBinder.next();
    labelColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getResource().getToolName()));

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeViewData>, String>
    descrColumn = columnBinder.next();
    descrColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getResource().getToolDescription()));

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeViewData>, String>
    rowActionColumn = columnBinder.next();
    DepanFxActionTableCell.prepareColumn(
        rowActionColumn, p -> new NodeViewsMergeActions());

    descrColumn.prefWidthProperty().bind(
        nodeViewsTable.widthProperty()
            .subtract(rsrcColumn.widthProperty())
            .subtract(labelColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(1));
  }

  @FXML
  public void handleAddNodeView() {
    addNodeView();
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Node Views Merge Confirmation Error";
  }

  @Override
  protected void checkInput(DepanFxProctor proctor) {
    super.checkInput(proctor);
    ObservableList<DepanFxWorkspaceResource<DepanFxNodeViewData>> srcs =
        nodeViewsTable.getItems();
    if (srcs.isEmpty()) {
      proctor.addError(
          "At least one input view required",
          "Node view merge requires at least one input view.");
    }
  }

  @Override
  protected DepanFxNodeViewData prepareResult() {
    ObservableList<DepanFxWorkspaceResource<DepanFxNodeViewData>> srcs =
        nodeViewsTable.getItems();
    DepanFxNodeViewData baseView = srcs.get(0).getResource();

    DepanFxNodeViewMerger merger =
        new DepanFxNodeViewMerger(baseView.getGraphDocRsrc());
    srcs.forEach(r -> merger.merge(r.getResource()));

    return merger.buildResult(getToolName(), getToolDescription(), baseView);
  }

  @Override
  protected void setTooldataFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(EXT_FILTER);
    chooser.setSelectedExtensionFilter(EXT_FILTER);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildAnalysisInitialDestination(EXT);
  }

  @Override
  protected String getDocumentName() {
    ObservableList<DepanFxWorkspaceResource<DepanFxNodeViewData>> srcs =
        nodeViewsTable.getItems();
    if (srcs.isEmpty()) {
      return "Empty";
    }
    String baseName = srcs.get(0).getResource().getToolName();
    int mergeSize = srcs.size();
    if (mergeSize == 1) {
      return baseName;
    }
    if (mergeSize < 100) {
      return baseName + "+" + String.valueOf(mergeSize - 1);
    }
    return baseName + "+++";
  }

  /////////////////////////////////////

  private ContextMenu buildNodeViewsMergeTableMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Add Node View", e -> addNodeView());
    return builder.build();
  }

  private void addNodeView() {
    DepanFxNodeViewChooser.runChooser(workspace, dialogRunner, getScene())
        .ifPresent(nv -> nodeViewsTable.getItems().add(nv));
  }

  private class NodeViewsMergeActions
      extends DepanFxActionTableCell<DepanFxWorkspaceResource<DepanFxNodeViewData>> {

    public NodeViewsMergeActions() {
      super(nodeViewsTable.getItems());
    }

    @Override
    protected void populateContextMenu(DepanFxContextMenuBuilder builder) {

      builder.appendActionItem("Select Node View...",
          e -> runNodeViewChooser(getIndex()));
      appendMoveOps(builder);
    }

    private void runNodeViewChooser(int index) {
      DepanFxNodeViewChooser.runChooser(workspace, dialogRunner, getScene())
          .ifPresent(nv -> setRow(index, nv));
    }
  }
}
