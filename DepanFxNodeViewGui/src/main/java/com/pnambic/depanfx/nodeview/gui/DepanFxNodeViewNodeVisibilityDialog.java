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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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

  private final DepanFxNodeFiltersDialogRegistry filterRegistry;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
      filterVisibilityTable;

  @SuppressWarnings("unused")
  private ObservableList<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
      filterVisibilityData;

  Map<DepanFxWorkspaceResource<DepanFxBaseFilterData>, BooleanProperty>
      filterVisibleProperties;

  private DepanFxNodeFilterSequenceData availableFilterDoc;

  private DepanFxNodeFilterSequenceData visibleFilterDoc;

  private NodeDisplayController nodeDisplay;

  private Consumer<DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>>
      onAvailableNodesRsrcUpdate;

  @Autowired
  public DepanFxNodeViewNodeVisibilityDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersDialogRegistry filterRegistry) {
    super(workspace, DepanFxNodeFilterSequenceData.class);
    this.dialogRunner = dialogRunner;
    this.filterRegistry = filterRegistry;
  }

  public static Dialog<DepanFxNodeViewNodeVisibilityDialog> runVisibilityDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeFilterSequenceData availableFilterDoc,
      DepanFxNodeFilterSequenceData visibleFilterDoc,
      NodeDisplayController nodeDisplay,
      Consumer<DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>>
          onAvailableNodesUpdate) {

    Dialog<DepanFxNodeViewNodeVisibilityDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            visibleFilterDoc, dialogRunner,
            DepanFxNodeViewNodeVisibilityDialog.class);

    DepanFxNodeViewNodeVisibilityDialog dlgCtrl = result.getController();
    dlgCtrl.setAvailableFilter(availableFilterDoc);
    dlgCtrl.setNodeDisplayController(nodeDisplay);
    dlgCtrl.setOnAvailableNodesUpdate(onAvailableNodesUpdate);
    result.runDialog(CREATE_NODE_FILTER_SEQUENCE);
    return result;
  }

  private void setNodeDisplayController(NodeDisplayController nodeDisplay) {
    this.nodeDisplay = nodeDisplay;
  }

  /**
   * The workspace resource for visible nodes is consumed during normal
   * dialog confirm and close.  This allows the container to save the
   * resource reference for the available nodes when it is changed in the
   * dialog.
   */
  public void setOnAvailableNodesUpdate(
      Consumer<DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>>
          onAvailableNodesRsrcUpdate) {
    this.onAvailableNodesRsrcUpdate = onAvailableNodesRsrcUpdate;
  }

  @FXML
  public void initialize() {
    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    columnBinder = new DepanFxTableColumnBinder<>(filterVisibilityTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseFilterData>, String>
    filePathColumn = columnBinder.next();
    filePathColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getResource().getToolName()));

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseFilterData>, Boolean>
    isVisibleColumn = columnBinder.next();
    isVisibleColumn.setCellValueFactory(
        r -> getFFilterVisibleProperty(r.getValue()));
    isVisibleColumn.setCellFactory(
        CheckBoxTableCell.forTableColumn(isVisibleColumn));

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseFilterData>, Number>
    countColumn = columnBinder.next();
    countColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
    countColumn.setCellValueFactory(
        r -> new SimpleIntegerProperty(
            nodeDisplay.getDisplayFilterNodeCount(
                r.getValue().getResource())));
  }

  /**
   * The visibility dialog is primarily an editor for the list of visible nodes.
   *
   * Although the visibility dialog also handles changes to the available
   * filters, those are separate from the "tool dialog".
   */
  @Override
  public void setTooldata(
      DepanFxNodeFilterSequenceData visibleFilterDoc) {
    super.setTooldata(visibleFilterDoc);
    this.visibleFilterDoc = visibleFilterDoc;

    installVisibleFilters();
  }

  public void setAvailableFilter(
      DepanFxNodeFilterSequenceData availableFilterDoc) {
    this.availableFilterDoc = availableFilterDoc;

    filterVisibleProperties = new HashMap<>();
    availableFilterDoc.streamFilterRefs()
        .forEach(m ->
            filterVisibleProperties.put(m, new SimpleBooleanProperty()));

    installVisibleFilters();

    ObservableList<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    availableFilters = FXCollections.observableArrayList();

    availableFilterDoc.streamFilterRefs()
        .forEach(availableFilters::add);

    filterVisibilityData = availableFilters;
    filterVisibilityTable.setItems(availableFilters);
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeFilterSequenceData prepareResult() {
    return new DepanFxNodeFilterSequenceData(
            getToolName(), getToolDescription(),
            visibleFilterDoc.getContextModelId(), prepareVisibleFilters());
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

  private BooleanProperty getFFilterVisibleProperty(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> value) {
    return filterVisibleProperties
        .getOrDefault(value, UNKNOWN_FILTER_VISIBILITY);
  }

  /////////////////////////////////////

  private void installVisibleFilters() {
    if (visibleFilterDoc == null || availableFilterDoc == null) {
      return;
    }

    visibleFilterDoc.streamFilterRefs()
        .map(m -> filterVisibleProperties.get(m))
        .forEach(b -> b.set(true));
  }

  /////////////////////////////////////
  // Context menu handlers

  @FXML
  private void handleApply() {
    DepanFxNodeFilterSequenceData result = prepareResult();
    nodeDisplay.clearFilterVisibility();
    result.streamFilterRefs()
        .map(r -> r.getResource())
        .forEach(m -> nodeDisplay.setFilterVisibility(m, true));
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
        .ifPresent(r -> setTooldata(r.getResource()));
  }

  @FXML
  private void handleSaveVisibleNodes() {
    handleConfirm();
  }

  @FXML
  private void handleSelectAvailableVisibleNodes() {
    DepanFxNodeFiltersSequenceChooser.runNodeFiltersFinder(
            workspace, dialogRunner, getScene(), filterRegistry)
        .ifPresent(r -> setAvailableFilter(r.getResource()));
  }

  @FXML
  private void handleSaveAvailableNodes() {
    Dialog<DepanFxNodeFilterSequenceToolDialog> saveAvailDlg =
        DepanFxNodeFilterSequenceToolDialog.runCreateDialog(
            prepareAvailableNodesResult(), dialogRunner);

    saveAvailDlg.getController().getWorkspaceResource()
        .ifPresent(this::updateAvailableNodes);
  }

  private List<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
      prepareVisibleFilters() {
    return filterVisibleProperties.entrySet().stream()
        .filter(e -> e.getValue().get())
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }

  private void updateAvailableNodes(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableFiltersRsrc) {
    // The resource might have changed in the create dialog.
    setAvailableFilter(availableFiltersRsrc.getResource());

    onAvailableNodesRsrcUpdate.accept(availableFiltersRsrc);
  }

  private DepanFxNodeFilterSequenceData prepareAvailableNodesResult() {
    List<DepanFxWorkspaceResource<DepanFxBaseFilterData>> availableFilters =
        new ArrayList<>(filterVisibleProperties.size());
    filterVisibleProperties.keySet().forEach(availableFilters::add);

    return new DepanFxNodeFilterSequenceData(
        availableFilterDoc.getToolName(),
        availableFilterDoc.getToolDescription(),
        availableFilterDoc.getContextModelId(), availableFilters);
  }
}
