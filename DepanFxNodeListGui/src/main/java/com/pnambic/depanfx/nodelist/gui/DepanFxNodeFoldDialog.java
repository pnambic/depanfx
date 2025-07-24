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
package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.sections.folds.DepanFxNodeFoldChooser;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeFoldController;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.perspective.DepanFxBaseDialog;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.scene.DepanFxActionTableCell;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@DepanFxFxmlDialog
@FxmlView("node-fold-dialog.fxml")
public class DepanFxNodeFoldDialog extends DepanFxBaseDialog {

  @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFoldDialog.class);

  public static final String NODE_FOLDING_ITEM = "Node Folding...";

  public static final String NODE_FOLDING_TITLE = "Node Folding";

  private static final String SELECT_NODE_FOLDER_ITEM = "Select Node Folder...";

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  nodeFoldTable;

  private ObservableList<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  nodeFoldData;

  // Where the changes happen.
  private DepanFxNodeFoldController nodeFoldings;

  public DepanFxNodeFoldDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace);
    this.dialogRunner = dialogRunner;
  }

  public static void runEditDialog(
      DepanFxNodeFoldController nodeFoldings,
      DepanFxDialogRunner dialogRunner) {

    Dialog<DepanFxNodeFoldDialog> editDlg =
        dialogRunner.createDialogAndParent(DepanFxNodeFoldDialog.class);
    editDlg.getController().setNodeFoldings(nodeFoldings);
    editDlg.runDialog(NODE_FOLDING_TITLE);
  }

  public void setNodeFoldings(DepanFxNodeFoldController nodeFoldings) {
    this.nodeFoldings = nodeFoldings;
    nodeFoldings.streamNodeFoldResources()
        .forEach(nodeFoldData::add);
  }

  @Override
  public Scene getScene() {
    return nodeFoldTable.getScene();
  }

  @Override
  protected void checkInput(DepanFxProctor proctor) {
  }

  @FXML
  public void initialize() {
    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
    columnBinder = new DepanFxTableColumnBinder<>(nodeFoldTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeFoldData>, String>
    labelColumn = columnBinder.next();
    labelColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getResource().getToolName()));

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeFoldData>, String>
    rsrcColumn = columnBinder.next();
    rsrcColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            DepanFxProjects.asReferenceLabel(workspace, r.getValue())));
    rsrcColumn.setCellFactory(
        r -> new DepanFxNodeFoldChooser.NodeFoldingCell<>(
            workspace, dialogRunner, getScene(),
            (y, f) -> updateRow(y.getIndex(), f)));

    TableColumn<DepanFxWorkspaceResource<DepanFxNodeFoldData>, String>
    rowActionColumn = columnBinder.next();

    DepanFxActionTableCell.prepareColumn(rowActionColumn, p -> new DisplayActions());

    // Size filePath to remaining room
    rsrcColumn.prefWidthProperty().bind(
        nodeFoldTable.widthProperty()
            .subtract(labelColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(1));

    nodeFoldData = FXCollections.observableArrayList();
    nodeFoldTable.setItems(nodeFoldData);
  }

  @FXML
  private void addNodeFoldRow() {
    DepanFxNodeFoldChooser.runNodeFoldingFinder(
        workspace, dialogRunner, getScene())
        .ifPresent(this::addNodeFoldRow);
  }

  private void addNodeFoldRow(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldRsrc) {
    nodeFoldData.add(foldRsrc);
    nodeFoldings.appendNodeFoldResource(foldRsrc);
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected String getInputCheckFailureText() {
    return  "Node Fold Save Confirmation Error";
  }

  /////////////////////////////////////
  // FXML handlers.

  @FXML
  protected void handleApply() {
  }

  @FXML
  protected void handleConfirm() {
  }

  @FXML
  protected void handleSelectAll() {
  }

  @FXML
  protected void handleClearSelection() {
  }

  @FXML
  protected void handleInvertSelection() {
  }

  private void updateRow(
      int index,
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldRsrc) {

    nodeFoldData.add(index, foldRsrc);
  }

  /////////////////////////////////////
  // Table Cell Classes

  private class DisplayActions
      extends DepanFxActionTableCell<DepanFxWorkspaceResource<DepanFxNodeFoldData>> {

    public DisplayActions() {
      super(nodeFoldData);
    }

    @Override
    protected void populateContextMenu(DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(SELECT_NODE_FOLDER_ITEM,
          e -> runNodeFoldChooser(getIndex()));
    }

    private void runNodeFoldChooser(int index) {
      DepanFxNodeFoldChooser.runNodeFoldingFinder(
          workspace, dialogRunner, getScene())
          .ifPresent(r -> updateRow(index, r));
    }
  }
}
