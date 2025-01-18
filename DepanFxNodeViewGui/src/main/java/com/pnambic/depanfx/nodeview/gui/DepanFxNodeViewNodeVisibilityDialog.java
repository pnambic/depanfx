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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFilterSequenceToolDialog;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersDialogRegistry;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersSequenceChooser;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeFilterSequenceData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("node-view-node-visibility-dialog.fxml")
public class DepanFxNodeViewNodeVisibilityDialog
    extends DepanFxBaseToolDialog<DepanFxNodeFilterSequenceData> {

  private static final SimpleBooleanProperty UNKNOWN_FILTER_VISIBILITY =
      new SimpleBooleanProperty(false);

  @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewNodeVisibilityDialog.class);

  public static final String EDIT_NODE_VISIBILITY =
      "Edit Node Visibility";

  public static final String CREATE_NODE_VISIBILITY =
      "New Node Visibility";

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

  private final DepanFxNodeFiltersDialogRegistry filterRegistry;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
      filterVisibilityTable;

  @SuppressWarnings("unused")
  private ObservableList<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
      filterVisibilityData;

  /**
   * Displayed state of the check boxes
   */
  Map<DepanFxWorkspaceResource<DepanFxBaseFilterData>, BooleanProperty>
      filterVisibleProperties;

  private NodeDisplayController nodeDisplay;

  @Autowired
  public DepanFxNodeViewNodeVisibilityDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersDialogRegistry filterRegistry) {
    super(workspace, DepanFxNodeFilterSequenceData.class);
    this.dialogRunner = dialogRunner;
    this.filterRegistry = filterRegistry;
  }

  /**
   * The visibility filter sequence is the tool resource.
   */
  public static Dialog<DepanFxNodeViewNodeVisibilityDialog> runVisibilityDialog(
      DepanFxDialogRunner dialogRunner, NodeDisplayController nodeDisplay) {

    Dialog<DepanFxNodeViewNodeVisibilityDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            nodeDisplay.forUpdateVisibleFilterResource(),
            dialogRunner,
            DepanFxNodeViewNodeVisibilityDialog.class);

    DepanFxNodeViewNodeVisibilityDialog dlgCtrl = result.getController();
    dlgCtrl.setNodeDisplayController(nodeDisplay);
    result.runDialog(CREATE_NODE_VISIBILITY);
    return result;
  }

  /**
   * This should be done first.
   */
  private void setNodeDisplayController(NodeDisplayController nodeDisplay) {
    this.nodeDisplay = nodeDisplay;
    installAvailableFilterResource();
  }

  @FXML
  public void initialize() {
    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    columnBinder = new DepanFxTableColumnBinder<>(filterVisibilityTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseFilterData>, String>
    filterNameColumn = columnBinder.next();
    filterNameColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getResource().getToolName()));

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseFilterData>, Boolean>
    isVisibleColumn = columnBinder.next();
    isVisibleColumn.setCellValueFactory(
        r -> getFilterVisibleProperty(r.getValue()));
    isVisibleColumn.setCellFactory(
        CheckBoxTableCell.forTableColumn(isVisibleColumn));

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseFilterData>, Number>
    countColumn = columnBinder.next();
    countColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
    countColumn.setCellValueFactory(
        r -> new SimpleIntegerProperty(
            nodeDisplay.getDisplayFilterNodeCount(r.getValue())));

    // Size filePath to remaining room
    filterNameColumn.prefWidthProperty().bind(
        filterVisibilityTable.widthProperty()
            .subtract(isVisibleColumn.widthProperty())
            .subtract(countColumn.widthProperty())
            .subtract(1));
  }

  /**
   * The visibility dialog is primarily an editor for the list of visible nodes.
   *
   * Although the visibility dialog also handles changes to the available
   * filters, those are separate from the "tool dialog".
   */
  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> visibleFilterRsrc) {
    super.setToolResource(visibleFilterRsrc);

    installVisibleFilters();
  }

  public void setAvailableFilterResource(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableFilterRsrc) {
    nodeDisplay.setAvailableResource(availableFilterRsrc);
    installAvailableFilterResource();
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeFilterSequenceData prepareResult() {
    // Build results from UX table, not current nodeDisplay.
    // .. even though these should be the same if live updates are working.
    return new DepanFxNodeFilterSequenceData(
            getToolName(), getToolDescription(),
            getToolResource().get().getResource().getContextModelId(),
            prepareVisibleFilters());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeFilterSequenceData.NODE_FILTER_SEQUENCE_TOOL_EXT,
        DepanFxBaseFilterData.NODE_FILTERS_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxNodeFilterSequenceToolDialog
        .setNodeFilterSequenceTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Visible Nodes Save Confirmation Error";
  }

  /////////////////////////////////////
  // Visibility properties and updates

  private BooleanProperty getFilterVisibleProperty(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> value) {
    return filterVisibleProperties
        .getOrDefault(value, UNKNOWN_FILTER_VISIBILITY);
  }

  private void installAvailableFilterResource() {

    filterVisibleProperties = new HashMap<>();
    nodeDisplay.forEachAvailablityFilter(this::installFilterProperty);

    installVisibleFilters();

    ObservableList<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    availableFilters = FXCollections.observableArrayList();

    nodeDisplay.forEachAvailablityFilter(availableFilters::add);

    filterVisibilityData = availableFilters;
    filterVisibilityTable.setItems(availableFilters);
  }

  private void installFilterProperty(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> availableFilterRsrc) {
    BooleanProperty visibilityProp = new SimpleBooleanProperty();
    visibilityProp.addListener(
        (e, o, n) -> updateVisibility(availableFilterRsrc, n));
    filterVisibleProperties.put(availableFilterRsrc, visibilityProp);
  }

  private void installVisibleFilters() {
    if (getToolResource().isEmpty() || nodeDisplay == null) {
      return;
    }

    getToolResource()
        .map(DepanFxWorkspaceResource::getResource)
        .get()
        .streamFilterRefs()
        .map(m -> filterVisibleProperties.get(m))
        .forEach(b -> b.set(true));
  }

  private void updateVisibility(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc,
      boolean isVisible) {
    nodeDisplay.setFilterVisibility(filterRsrc, isVisible);
  }

  /////////////////////////////////////
  // Context menu handlers

  @FXML
  protected void handleRevert() {
    handleCancel();
    nodeDisplay.revertNodeDisplay();
  }

  @FXML
  private void handleApply() {
    nodeDisplay.setVisiblityResource(
        DepanFxWorkspaceResource.forUpdate(
            getToolResource().get(), prepareResult()));
  }

  @FXML
  private void handleSelectAll() {
    filterVisibleProperties.values().stream()
        .forEach(p -> p.set(true));
  }

  @FXML
  private void handleClearSelection() {
    filterVisibleProperties.values().stream()
        .forEach(p -> p.set(false));
  }

  @FXML
  private void handleInvertSelection() {
    filterVisibleProperties.values().stream()
        .forEach(p -> p.set(!p.get()));
  }

  @FXML
  private void handleSelectVisibleNodes() {
    DepanFxNodeFiltersSequenceChooser.runNodeFiltersFinder(
            workspace, dialogRunner, getScene(), filterRegistry)
        .ifPresent(this::setToolResource);
  }

  @FXML
  private void handleSaveVisibleNodes() {
    handleConfirm();
  }

  @FXML
  private void handleSelectAvailableVisibleNodes() {
    DepanFxNodeFiltersSequenceChooser.runNodeFiltersFinder(
            workspace, dialogRunner, getScene(), filterRegistry)
        .ifPresent(this::setAvailableFilterResource);
  }

  @FXML
  private void handleSaveAvailableNodes() {
    DepanFxNodeFilterSequenceToolDialog.runCreateDialog(
        nodeDisplay.forUpdateAvailableFilterResource(),
        dialogRunner)
        .getController()
        .getToolResource()
        .ifPresent(this::setAvailableFilterResource);
  }

  private List<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
      prepareVisibleFilters() {
    return filterVisibleProperties.entrySet().stream()
        .filter(e -> e.getValue().get())
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }
}
