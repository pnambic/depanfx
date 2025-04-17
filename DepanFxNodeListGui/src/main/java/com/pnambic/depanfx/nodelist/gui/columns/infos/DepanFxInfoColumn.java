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
package com.pnambic.depanfx.nodelist.gui.columns.infos;

import com.pnambic.depanfx.graph.info.GraphNodeInfo.Listener;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry.Contribution;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry.PropertyStore;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxAbstractColumn;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;

public class DepanFxInfoColumn
    extends DepanFxAbstractColumn<DepanFxNodeInfoColumnData> {

  private static final String ALIGN_CENTER_RIGHT = "-fx-alignment: CENTER-RIGHT;";

  public static final String EDIT_INFO_COLUMN = "Edit Info Column...";

  public static final String NEW_INFO_COLUMN = "New Info Column...";

  public static final String SELECT_INFO_COLUMN = "Select Info Column...";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxInfoColumn.class);

  private final DepanFxInfoRegistry.PropertyStore infoStore;

  public DepanFxInfoColumn(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnDataRsrc) {
    super(tableAdapter, columnDataRsrc);
    infoStore = null;
  }

  public DepanFxInfoColumn(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnDataRsrc,
      PropertyStore infoStore) {
    super(tableAdapter, columnDataRsrc);
    // TODO: Hoist this to an infoStore, or refuse .. null info store?
    this.infoStore = infoStore;
  }

  @Override
  public void prepareCell(TreeTableCell<DepanFxNodeListMember, ?> cell) {
    super.prepareCell(cell);
    getCellStyle().ifPresent(cell::setStyle);
    cell.setEditable(getInfoProperty().isEditable());
  }

  private Optional<String> getCellStyle() {
    // Can't be defined as a trait of property kind, 'cuz infos
    // are a core Graph interface.
    switch (getInfoProperty().getPropertyKind()) {
    case INT:
    case POS:
      return Optional.of(ALIGN_CENTER_RIGHT);
    case STRING:
    }

    return Optional.empty();
  }

  @Override
  public ContextMenu buildColumnContextMenu(DepanFxDialogRunner dialogRunner) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(SELECT_INFO_COLUMN,
        e -> openColumnChooser(dialogRunner));
    builder.appendActionItem(EDIT_INFO_COLUMN,
        e -> openColumnEditor(dialogRunner));
    return builder.build();
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    try {
      return getPropertyString(member.getGraphNode());
    } catch (Exception errAny) {
      LOG.info("Trouble rendering {} due to {}",
          member.getDisplayName(), errAny.getMessage());
    }
    return null;
  }

  public String toString(GraphNode node) {
    try {
      return getPropertyString(node);
    } catch (Exception errAny) {
      LOG.info("Trouble rendering {} property {} due to {}",
          node.getId(), getInfoContribution().getInfoLabel(),
          errAny.getMessage());
    }
    return null;
  }

  /**
   * Validate that the input string is an acceptable value for the field.
   */
  public String cleanInput(String input) {
    return getInfoProperty().getPropertyKind().clean(input);
  }

  public void commitEdit(DepanFxNodeListGraphNode member, String input) {
    setPropertyValue(member.getGraphNode(), input);
  }

  public Optional<?> getPropertyValue(GraphNode graphNode) {
    return getInfoContribution().getPropertyValue(
        infoStore, graphNode, getInfoProperty());
  }

  public void setPropertyValue(GraphNode graphNode, String input) {
    getInfoContribution().setPropertyValue(
        infoStore, graphNode, getInfoProperty(), input);
  }

  public void addInfoListener(GraphNode graphNode, Listener listener) {
    getInfoContribution().addInfoListener(
        infoStore, graphNode, getInfoProperty(), listener);
  }

  public void removeInfoListener(GraphNode graphNode, Listener listener) {
    getInfoContribution().removeInfoListener(
        infoStore, graphNode, getInfoProperty(), listener);
  }

  public void addNewColumnAction(
      DepanFxContextMenuBuilder builder,
      DepanFxDialogRunner dialogRunner) {
    builder.appendActionItem(NEW_INFO_COLUMN,
        e -> openColumnCreate(dialogRunner));
  }

  @Override
  protected TreeTableColumn<DepanFxNodeListMember, ?> buildColumn() {
    if (getInfoProperty().isEditable()) {
      TreeTableColumn<DepanFxNodeListMember, String> result =
          new TreeTableColumn<>(getColumnLabel());
      result.setCellFactory(p -> new DepanFxEditInfoColumnCell(this));
      result.setCellValueFactory(p -> buildObservedInfo(p));
      return result;
    }

    TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> result =
        new TreeTableColumn<>(getColumnLabel());
    result.setCellFactory(p -> new DepanFxDisplayInfoColumnCell(this));
    result.setCellValueFactory(p ->
        new ReadOnlyObjectWrapper<>(p.getValue().getValue()));
    return result;
  }

  private ObservableValue<String> buildObservedInfo(
      CellDataFeatures<DepanFxNodeListMember, String> p) {
    DepanFxNodeListMember member = p.getValue().getValue();
    if (member instanceof DepanFxNodeListGraphNode node) {
      ReadOnlyObjectWrapper<String> result =
          new ReadOnlyObjectWrapper<>(toString(node));
      getInfoContribution().addInfoListener(
          infoStore, node.getGraphNode(), null, 
          (n, i) -> result.setValue(getPropertyString(n)));
      return result;
    }

    return null;
  }

  private void openColumnCreate(DepanFxDialogRunner dialogRunner) {
    DepanFxNodeInfoColumnData initialData =
        DepanFxNodeInfoColumnData.buildInitialColumnData();
    DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc =
        tableAdapter.getWorkspace().addScratchResource(initialData);
    DepanFxInfoColumnToolDialog.runCreateDialog(columnRsrc, dialogRunner);
  }

  private void openColumnEditor(DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxInfoColumnToolDialog> nodeKeyColumnEditor =
          DepanFxInfoColumnToolDialog.runEditDialog(
              forUpdate(buildEditData()), dialogRunner, tableAdapter);

    nodeKeyColumnEditor.getController().getToolResource()
        .ifPresent(this::updateColumnDataRsrc);
  }

  private DepanFxNodeInfoColumnData buildEditData() {
    DepanFxNodeInfoColumnData columnData = getColumnData();

    int widthMs = (int) Math.round(
        column.getWidth() / DepanFxSceneControls.layoutWidthMs(1));
    return new DepanFxNodeInfoColumnData(
        columnData.getToolName(), columnData.getToolDescription(),
        columnData.getColumnLabel(), widthMs,
        columnData.getInfoContribution(), columnData.getInfoProperty());
  }

  private void openColumnChooser(DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser columnChooser = prepareChooser(dialogRunner);

    DepanFxWorkspace workspace = tableAdapter.getWorkspace();
    columnChooser.showOpenDialog(getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxNodeInfoColumnData.class))
        .ifPresent(this::updateColumnDataRsrc);
  }

  private DepanFxResourceChooser prepareChooser(
      DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(tableAdapter.getWorkspace(), dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);

    result.getExtensionFilters().add(
        DepanFxInfoColumnToolDialog.INFO_COLUMN_RSRC_FILTER);
    result.setSelectedExtensionFilter(
        DepanFxInfoColumnToolDialog.INFO_COLUMN_RSRC_FILTER);
    return result;
  }

  private String getPropertyString(GraphNode graphNode) {
    return getPropertyValue(graphNode)
        .map(v -> getInfoString(v))
        .orElse("");
  }

  private String getInfoString(Object value) {
      return getInfoProperty().getPropertyKind().toString(value);
  }

  private DepanFxNodeInfoProperty getInfoProperty() {
    return getColumnData().getInfoProperty();
  }

  private Contribution getInfoContribution() {
    return getColumnData().getInfoContribution();
  }
}
