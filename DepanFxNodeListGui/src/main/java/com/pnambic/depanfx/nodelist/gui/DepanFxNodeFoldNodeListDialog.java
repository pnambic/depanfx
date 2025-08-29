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

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.perspective.DepanFxBaseDialog;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.scene.DepanFxActionTableCell;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

@DepanFxFxmlDialog
@FxmlView("node-fold-node-list-dialog.fxml")
public class DepanFxNodeFoldNodeListDialog extends DepanFxBaseDialog {

  public static final String FOLD_SELECTED_NODES_TITLE = "Fold Selected Nodes";

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField nestNameField;

  @FXML
  private TextField nestKeyField;

  private GraphNode nestNode;

  @FXML
  private TableView<GraphNode> nodeListTable;

  private ObservableList<GraphNode> nodeListData;

  @FXML
  private ComboBox<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  foldIntoCombo;

  private DepanFxNodeListTableAdapter tableAdapter;

  @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFoldNodeListDialog.class);

  public DepanFxNodeFoldNodeListDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace);
    this.dialogRunner = dialogRunner;
  }

  public static void runNodeFoldDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxNodeList depanFxNodeList) {

    Dialog<DepanFxNodeFoldNodeListDialog> editDlg =
        dialogRunner.createDialogAndParent(DepanFxNodeFoldNodeListDialog.class);
    editDlg.getController().setTable(tableAdapter);
    editDlg.runDialog(FOLD_SELECTED_NODES_TITLE);
  }

  @Override
  public Scene getScene() {
    return nodeListTable.getScene();
  }

  @Override
  protected void checkInput(DepanFxProctor proctor) {
  }

  @FXML
  public void initialize() {
    DepanFxTableColumnBinder<GraphNode> columnBinder =
        new DepanFxTableColumnBinder<>(nodeListTable);

    TableColumn<GraphNode, String> nameColumn = columnBinder.next();
    nameColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getId().getSimpleName()));

    TableColumn<GraphNode, String> keyColumn = columnBinder.next();
    keyColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getId().getNodeKey()));

    TableColumn<GraphNode, String> rowActionColumn = columnBinder.next();
    DepanFxActionTableCell.prepareColumn(
        rowActionColumn, p -> new DisplayActions());

    // Size filePath to remaining room
    keyColumn.prefWidthProperty().bind(
        nodeListTable.widthProperty()
            .subtract(nameColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(1));

    nodeListData = FXCollections.observableArrayList();
    nodeListTable.setItems(nodeListData);
  }

  public void setTable(DepanFxNodeListTableAdapter tableAdapter) {
    this.tableAdapter = tableAdapter;
    populateFoldCombo();
  }

  public void setNodeList(DepanFxNodeList nodeList) {
    nodeListData.setAll(nodeList.getNodes());
    clearNodeFoldNest();
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected String getInputCheckFailureText() {
    return  "Node List Fold Confirmation Error";
  }

  /////////////////////////////////////
  // FXML handlers.

  @FXML
  protected void handleConfirm() {
  }

  /////////////////////////////////////
  // Table Cell Classes

  private class DisplayActions
      extends DepanFxActionTableCell<GraphNode> {

    private static final String USE_AS_NEST = null;

    public DisplayActions() {
      super(nodeListData);
    }

    @Override
    protected void populateContextMenu(DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(USE_AS_NEST,
          e -> selectNest(getIndex()));
    }
  }

  private void selectNest(int nestIndex) {
    GraphNode originalNest = nestNode;
    if (nestIndex >= nodeListData.size()) {
      return;
    }
    GraphNode chosenNest = nodeListData.get(nestIndex);
    nodeListData.remove(nestIndex);
    if (originalNest != null) {
      nodeListData.add(originalNest);
    }
    setNodeFoldNest(chosenNest);
  }

  private void setNodeFoldNest(GraphNode chosenNest) {
    nestNode = chosenNest;
    nestNameField.setText(chosenNest.getId().getSimpleName());
    nestKeyField.setText(chosenNest.getId().getNodeKey());
  }

  public void clearNodeFoldNest() {
    nestNode = null;
    // Allow prompt to show ..
    nestNameField.setText(null);
    nestKeyField.setText(null);
  }

  private void populateFoldCombo() {
    ObservableList<DepanFxWorkspaceResource<DepanFxNodeFoldData>> items =
        foldIntoCombo.getItems();
    tableAdapter.streamNodeFoldResources()
        .forEach(items::add);
    foldIntoCombo.setConverter(new FoldResouceConverter());
  }

  private static class FoldResouceConverter
      extends StringConverter<DepanFxWorkspaceResource<DepanFxNodeFoldData>> {

    @Override
    public String toString(
        DepanFxWorkspaceResource<DepanFxNodeFoldData> foldRsrc) {
      return foldRsrc.getResource().getToolName();
    }

    @Override
    public DepanFxWorkspaceResource<DepanFxNodeFoldData> fromString(
        String string) {
      throw new UnsupportedOperationException();
    }
  }
}
