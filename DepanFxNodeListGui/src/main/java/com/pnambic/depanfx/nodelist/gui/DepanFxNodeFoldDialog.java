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
import com.pnambic.depanfx.nodelist.gui.sections.folds.DepanFxNodeFoldToolDialog;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeFoldController;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.perspective.DepanFxWorkspaceDialog;
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

import java.io.IOException;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * All changes are live on the attached DepanFxNodeFoldController.
 * No cancel, apply, or confirm actions are useful.
 *
 * A close button is available as a visual cue for closing the dialog.
 */
@DepanFxFxmlDialog
@FxmlView("node-fold-dialog.fxml")
public class DepanFxNodeFoldDialog extends DepanFxWorkspaceDialog {

  // @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFoldDialog.class);

  public static final String NODE_FOLDING_ITEM = "Node Folding...";

  public static final String NODE_FOLDING_TITLE = "Node Folding";

  public static final String SELECT_NODE_FOLDING_ITEM =
      "Select Node Folding...";

  public static final String SAVE_NODE_FOLDER_ITEM = "Save Node Folding";

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  captureFoldTable;

  private ObservableList<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  captureFoldData;

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
    nodeFoldings.streamCaptureFoldResources()
        .forEach(captureFoldData::add);
  }

  @Override
  public Scene getScene() {
    return captureFoldTable.getScene();
  }

  @FXML
  public void initialize() {
    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
    columnBinder = new DepanFxTableColumnBinder<>(captureFoldTable);

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
        captureFoldTable.widthProperty()
            .subtract(labelColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(1));

    captureFoldData = FXCollections.observableArrayList();
    captureFoldTable.setItems(captureFoldData);
  }

  @FXML
  public void onNewNodeFolding() {
    DepanFxWorkspaceResource<DepanFxNodeFoldData> foldInfo =
        nodeFoldings.createFoldResource(workspace);

    DepanFxNodeFoldToolDialog.runCreateDialog(
        workspace, dialogRunner, foldInfo)
        .getController()
        .getToolResource()
        .ifPresent(this::addNodeFoldRow);
  }

  @FXML
  public void saveAllFoldings() {
    nodeFoldings.streamCaptureFoldResources()
        .flatMap(r -> saveNodeFolding(r).stream())
        .forEach(r -> {
          int index = findFoldingResourceIndex(r);
          if (index >= 0) {
            updateRow(index, r);
          }
        });
  }

  @FXML
  private void addNodeFoldRow() {
    DepanFxNodeFoldChooser.runNodeFoldingFinder(
        workspace, dialogRunner, getScene())
        .ifPresent(this::addNodeFoldRow);
  }

  private void addNodeFoldRow(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldRsrc) {
    captureFoldData.add(foldRsrc);
    nodeFoldings.appendCaptureFoldResource(foldRsrc);
  }

  private void updateRow(
      int index,
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldRsrc) {

    captureFoldData.set(index, foldRsrc);
    nodeFoldings.installCaptureFoldResourceAt(index, foldRsrc);
  }

  private void saveIndexedFolding(int index) {
    saveNodeFolding(nodeFoldings.forUpdateCaptureFoldResourceAt(index))
        .ifPresent(r -> updateRow(index, r));
  }

  private Optional<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  saveNodeFolding(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldUpdateRsrc) {

    try {
      return getWorkspace().saveDocument(
          foldUpdateRsrc.getDocument(), foldUpdateRsrc.getResource());
    } catch (IOException err) {
      LOG.error("Unable to update fold info for document {}",
          DepanFxProjects.getDocumentLabel(foldUpdateRsrc.getDocument()),err);
    }
    return Optional.empty();
  }

  private int findFoldingResourceIndex(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldDataRsrc) {
    for (int i = 0; i < captureFoldData.size(); ++i) {
      if (captureFoldData.get(i).getDocument().equals(foldDataRsrc.getDocument())) {
        return i;
      }
    }
    return -1;
  }

  /////////////////////////////////////
  // Table Cell Classes

  private class DisplayActions
      extends DepanFxActionTableCell<DepanFxWorkspaceResource<DepanFxNodeFoldData>> {

    public DisplayActions() {
      super(captureFoldData);
    }

    @Override
    protected void populateContextMenu(DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(SELECT_NODE_FOLDING_ITEM,
          e -> runNodeFoldChooser(getIndex()));
      builder.appendActionItem(SAVE_NODE_FOLDER_ITEM,
          e -> saveIndexedFolding(getIndex()));
    }

    private void runNodeFoldChooser(int index) {
      DepanFxNodeFoldChooser.runNodeFoldingFinder(
          workspace, dialogRunner, getScene())
          .ifPresent(r -> updateRow(index, r));
    }
  }
}
