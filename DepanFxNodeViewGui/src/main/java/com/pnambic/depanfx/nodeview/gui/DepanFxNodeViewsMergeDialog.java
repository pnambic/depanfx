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

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.perspective.DepanFxBaseDialog;
import com.pnambic.depanfx.perspective.DepanFxDialogChecks;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.scene.DepanFxActionTableCell;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

@DepanFxFxmlDialog
@FxmlView("node-views-merge-dialog.fxml")
public class DepanFxNodeViewsMergeDialog extends DepanFxBaseDialog {

  private static final Logger LOG = LoggerFactory.getLogger(
      DepanFxNodeViewsMergeDialog.class);

  public static final String MERGE_NODE_VIEWS = "Merge Node Views";

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxNodeViewData>> nodeViewsTable;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxNodeViewsMergeDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace);
    this.dialogRunner = dialogRunner;
  }

  public static DepanFxNodeViewsMergeDialog runMergeDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {

    Dialog<DepanFxNodeViewsMergeDialog> result =
        dialogRunner.createDialogAndParent(DepanFxNodeViewsMergeDialog.class);
    result.runDialog(MERGE_NODE_VIEWS);

    return result.getController();
  }

  @FXML
  public void initialize() {
    nodeViewsTable.setContextMenu(buildNodeViewsMergeTableMenu());

    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxNodeViewData>>
    columnBinder =
        new DepanFxTableColumnBinder<>(nodeViewsTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeViewData>, String>
    labelColumn =
        columnBinder.next();

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeViewData>, String>
    descrColumn =
        columnBinder.next();

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeViewData>, String>
    rowActionColumn =
        columnBinder.next();
    DepanFxActionTableCell.prepareColumn(
        rowActionColumn, p -> new NodeViewsMergeActions());

    descrColumn.prefWidthProperty().bind(
        nodeViewsTable.widthProperty()
            .subtract(labelColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(1));
  }

  @FXML
  public void handleAddNodeView() {
    addNodeView();
  }

  @FXML
  public void openDestinationChooser() {
    LOG.info("open destination chooser");
  }

  @FXML
  private void handleMerge() {
    if (hasInputErrors()) {
      return;
    }

    closeDialog();
    LOG.info("Would merge here");
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Node Views Merge Confirmation Error";
  }

  @Override
  protected void checkInput(DepanFxProctor proctor) {
    DepanFxDialogChecks.checkDestinationFile(
        proctor, destinationField.getText());
  }

  @Override
  public Scene getScene() {
    return nodeViewsTable.getScene();
  }

  /////////////////////////////////////

  private ContextMenu buildNodeViewsMergeTableMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Add Node View", e -> addNodeView());
    return builder.build();
  }

  private void addNodeView() {
    DepanFxNodeListChooser.runNodeListChooser(
        workspace, dialogRunner, getScene())
        .ifPresent(nv -> LOG.info("Should pick a view"));
    //  .ifPresent(nv -> nodeViewsTable.getItems().add(nv));
  }

  private class NodeViewsMergeActions
      extends DepanFxActionTableCell<DepanFxWorkspaceResource<DepanFxNodeViewData>> {

    public NodeViewsMergeActions() {
      super(nodeViewsTable.getItems());
    }

    @Override
    protected void populateContextMenu(DepanFxContextMenuBuilder builder) {
      appendMoveOps(builder);
    }
  }
}
