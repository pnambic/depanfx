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
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxActionCell;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("filter-sequence-tool-dialog.fxml")
public class DepanFxNodeFilterSequenceToolDialog
    extends DepanFxBaseToolDialog<DepanFxNodeFilterSequenceData> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFilterSequenceToolDialog.class);

  public static final String EDIT_NODE_FILTER_SEQUENCE =
      "Edit Node Filter Sequence";

  public static final String CREATE_NODE_FILTER_SEQUENCE =
      "New Node Filter Sequence";

  public static final ExtensionFilter NODE_FILTER_SEQUENCE_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Node Filter Sequence",
          DepanFxNodeFilterSequenceData.NODE_FILTER_SEQUENCE_TOOL_EXT);

  public static final DepanFxResourceFilter NODE_FILTER_SEQUENCE_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Node Filter Sequence",
          DepanFxNodeFilterSequenceData.NODE_FILTER_SEQUENCE_TOOL_EXT,
          DepanFxNodeFilterSequenceData.class);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private Label nodeFiltersLabel;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
      nodeFiltersSequenceTable;

  private ObservableList<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
      nodeFiltersSequenceData;

  // Retain for internal properties, like graph model.
  // private DepanFxNodeFilterSequenceData filterSeqDoc;

  private final DepanFxNodeFiltersDialogRegistry nodeFilterDialogRegistry;

  @Autowired
  public DepanFxNodeFilterSequenceToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersDialogRegistry nodeFilterDialogRegistry) {
    super(workspace, DepanFxNodeFilterSequenceData.class);
    this.dialogRunner = dialogRunner;
    this.nodeFilterDialogRegistry = nodeFilterDialogRegistry;
  }

  public static Dialog<DepanFxNodeFilterSequenceToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> filterSeqRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        filterSeqRsrc, dialogRunner,
        DepanFxNodeFilterSequenceToolDialog.class,
        EDIT_NODE_FILTER_SEQUENCE);
  }

  public static Dialog<DepanFxNodeFilterSequenceToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> filterSeqRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        filterSeqRsrc, dialogRunner,
        DepanFxNodeFilterSequenceToolDialog.class,
        CREATE_NODE_FILTER_SEQUENCE);
  }

  public static void setNodeFilterSequenceTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(NODE_FILTER_SEQUENCE_FILTER);
    result.setSelectedExtensionFilter(NODE_FILTER_SEQUENCE_FILTER);
  }

  @FXML
  public void initialize() {
    nodeFiltersLabel.setContextMenu(buildNodeFiltersMenu());

    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    columnBinder = new DepanFxTableColumnBinder<>(nodeFiltersSequenceTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseFilterData>, String>
    resourceColumn = columnBinder.next();
    resourceColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getDocument().getMemberPath().toString()));

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseFilterData>, String>
    filePathColumn = columnBinder.next();
    filePathColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getResource().getToolName()));

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseFilterData>, String>
    rowActionColumn = columnBinder.next();
    DepanFxActionCell.prepareColumn(rowActionColumn, p -> new FilterActions());

    // Size the resource column to remaining room
    resourceColumn.prefWidthProperty().bind(
        nodeFiltersSequenceTable.widthProperty()
            .subtract(filePathColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(2));
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> filterSeqRsrc) {
    super.setToolResource(filterSeqRsrc);

    nodeFiltersSequenceData = FXCollections.observableArrayList();
    filterSeqRsrc.getResource().streamFilterRefs()
        .forEach(nodeFiltersSequenceData::add);
    nodeFiltersSequenceTable.setItems(nodeFiltersSequenceData);
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeFilterSequenceData prepareResult() {
    // Ensure the use of a serializable ArrayList.
    List<DepanFxWorkspaceResource<DepanFxBaseFilterData>> filters =
        new ArrayList<>(nodeFiltersSequenceData.size());
    nodeFiltersSequenceData.forEach(filters::add);

    DepanFxNodeFilterSequenceData filterSeqInfo =
        getToolResource().get().getResource();
    return new DepanFxNodeFilterSequenceData(
            getToolName(), getToolDescription(),
            filterSeqInfo .getContextModelId(), filters);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeFilterSequenceData.NODE_FILTER_SEQUENCE_TOOL_EXT,
        DepanFxBaseFilterData.NODE_FILTERS_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxNodeFilterSequenceToolDialog.setNodeFilterSequenceTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Node Filter Sequence Save Confirmation Error";
  }

  /////////////////////////////////////
  // Internal Table Classes

  @FXML
  private void addNodeFilter() {
    DepanFxNodeFiltersChooser.runNodeFiltersFinder(
            getWorkspace(), dialogRunner, getScene(), nodeFilterDialogRegistry)
        .ifPresent(nodeFiltersSequenceData::add);
  }

  private ContextMenu buildNodeFiltersMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Add Node Filter...", e -> addNodeFilter());
    return builder.build();
  }

  private class FilterActions
      extends DepanFxActionCell<DepanFxWorkspaceResource<DepanFxBaseFilterData>> {

    public FilterActions() {
      super(nodeFiltersSequenceData);
    }

    @Override
    protected void populateContextMenu(DepanFxContextMenuBuilder builder) {

      builder.appendActionItem("Select Filter...",
          e -> runFilterChooser(getIndex()));

      appendMoveOps(builder);
    }

    private void runFilterChooser(int index) {
      DepanFxNodeFiltersChooser.runNodeFiltersFinder(
          getWorkspace(), dialogRunner, getScene(), nodeFilterDialogRegistry)
          .ifPresent(r -> setRow(index, r));
    }
  }
}
