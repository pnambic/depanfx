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

import com.pnambic.depanfx.nodelist.gui.DepanFxLinkMatcherSequenceChooser;
import com.pnambic.depanfx.nodelist.gui.DepanFxLinkMatcherSequenceToolDialog;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherSequenceDocument;
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
@FxmlView("node-view-edge-visibility-dialog.fxml")
public class DepanFxNodeViewEdgeVisibilityDialog
    extends DepanFxBaseToolDialog<DepanFxLinkMatcherSequenceDocument> {

  private static final SimpleBooleanProperty UNKNOWN_MATCHER_VISIBILITY =
      new SimpleBooleanProperty(false);

  @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewEdgeVisibilityDialog.class);

  public static final String EDIT_LINK_MATCHER_SEQUENCE =
      "Edit Link Matcher Sequence";

  public static final String CREATE_LINK_MATCHER_SEQUENCE =
      "New Link Matcher Sequence";

  public static final ExtensionFilter LINK_MATCHER_SEQUENCE_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Link Matcher Sequence",
          DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_EXT);

  public static final DepanFxResourceFilter LINK_MATCHER_SEQUENCE_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Link Matcher Sequence",
          DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_EXT,
          DepanFxLinkMatcherSequenceDocument.class);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      matcherVisibilityTable;

  @SuppressWarnings("unused")
  private ObservableList<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      matcherVisibilityData;

  Map<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, BooleanProperty>
      matcherVisibleProperties;

  private DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc;

  private EdgeDisplayController edgeDisplay;

  private Consumer<DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>>
      onAvailableEdgesRsrcUpdate;

  @Autowired
  public DepanFxNodeViewEdgeVisibilityDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxLinkMatcherSequenceDocument.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxNodeViewEdgeVisibilityDialog> runVisibilityDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc,
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> matcherSequenceRsrc,
      EdgeDisplayController edgeDisplay,
      Consumer<DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>>
          onAvailableEdgesUpdate) {

    Dialog<DepanFxNodeViewEdgeVisibilityDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            matcherSequenceRsrc, dialogRunner,
            DepanFxNodeViewEdgeVisibilityDialog.class);

    DepanFxNodeViewEdgeVisibilityDialog dlgCtrl = result.getController();
    dlgCtrl.setAvailableMatchers(availableMatchersRsrc);
    dlgCtrl.setEdgeDisplayController(edgeDisplay);
    dlgCtrl.setOnAvailableEdgesUpdate(onAvailableEdgesUpdate);
    result.runDialog(CREATE_LINK_MATCHER_SEQUENCE);
    return result;
  }

  private void setEdgeDisplayController(EdgeDisplayController edgeDisplay) {
    this.edgeDisplay = edgeDisplay;
  }

  /**
   * The workspace resource for visible edges is consumed during normal
   * dialog confirm and close.  This allows the container to save the
   * resource reference for the available edges when it is changed in the
   * dialog.
   */
  public void setOnAvailableEdgesUpdate(
      Consumer<DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>>
          onAvailableEdgesRsrcUpdate) {
    this.onAvailableEdgesRsrcUpdate = onAvailableEdgesRsrcUpdate;
  }

  @FXML
  public void initialize() {
    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
    columnBinder = new DepanFxTableColumnBinder<>(matcherVisibilityTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, String>
    filePathColumn = columnBinder.next();
    filePathColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getResource().getToolName()));

    TableColumn<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, Boolean>
    isVisibleColumn = columnBinder.next();
    isVisibleColumn.setCellValueFactory(
        r -> getMatcherVisibleProperty(r.getValue()));
    isVisibleColumn.setCellFactory(
        CheckBoxTableCell.forTableColumn(isVisibleColumn));

    TableColumn<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, Number>
    countColumn = columnBinder.next();
    countColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
    countColumn.setCellValueFactory(
        r -> new SimpleIntegerProperty(
            edgeDisplay.getDisplayMatcherEdgeCount(
                r.getValue().getResource())));
  }

  /**
   * For visibility dialog, tool data handles the visible matchers,
   * and the available matchers are handled separately.
   */

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleMatcherRsrc) {
    super.setToolResource(visibleMatcherRsrc);

    installVisibleMatchers();
  }

  public void setAvailableMatchers(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc) {
    this.availableMatchersRsrc = availableMatchersRsrc;

    matcherVisibleProperties = new HashMap<>();
    availableMatchersRsrc.getResource().streamMatchers()
        .forEach(m -> 
            matcherVisibleProperties.put(m, new SimpleBooleanProperty()));

    installVisibleMatchers();

    ObservableList<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
    availableMatchers = FXCollections.observableArrayList();

    availableMatchersRsrc.getResource().streamMatchers()
        .forEach(availableMatchers::add);

    matcherVisibilityData = availableMatchers;
    matcherVisibilityTable.setItems(availableMatchers);
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxLinkMatcherSequenceDocument prepareResult() {
    return new DepanFxLinkMatcherSequenceDocument(
            getToolName(), getToolDescription(),
            getToolResource().get().getResource().getModelId(),
            prepareVisibleMatchers());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_EXT,
        DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxLinkMatcherSequenceToolDialog
        .setLinkMatcherSequenceTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Visible Edges Save Confirmation Error";
  }

  private BooleanProperty getMatcherVisibleProperty(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> value) {
    return matcherVisibleProperties
        .getOrDefault(value, UNKNOWN_MATCHER_VISIBILITY);
  }

  /////////////////////////////////////

  private void installVisibleMatchers() {
    if (getToolResource() == null || availableMatchersRsrc == null) {
      return;
    }

    getToolResource()
        .map(DepanFxWorkspaceResource::getResource)
        .get()
        .streamMatchers()
        .map(m -> matcherVisibleProperties.get(m))
        .forEach(b -> b.set(true));
  }

  /////////////////////////////////////
  // Context menu handlers

  @FXML
  private void handleApply() {
    DepanFxLinkMatcherSequenceDocument result = prepareResult();
    edgeDisplay.clearMatcherVisibility();
    result.streamMatchers()
        .map(r -> r.getResource())
        .forEach(m -> edgeDisplay.setMatcherVisibility(m, true));
  }

  @FXML
  private void handleSelectAll() {
    matcherVisibleProperties.values().stream()
        .forEach(p -> p.set(true));
  }

  @FXML
  private void handleClearSelection() {
    matcherVisibleProperties.values().stream()
    .forEach(p -> p.set(false));
  }

  @FXML
  private void handleInvertSelection() {
    matcherVisibleProperties.values().stream()
        .forEach(p -> p.set(!p.get()));
  }

  @FXML
  private void handleSelectVisibleEdges() {
    DepanFxLinkMatcherSequenceChooser.runChooser(
            workspace, dialogRunner, getScene())
        .ifPresent(this::setToolResource);
  }

  @FXML
  private void handleSaveVisibleEdges() {
    handleConfirm();
  }

  @FXML
  private void handleSelectAvailableVisibleEdges() {
    DepanFxLinkMatcherSequenceChooser.runChooser(
            workspace, dialogRunner, getScene())
        .ifPresent(this::setAvailableMatchers);
  }

  @FXML
  private void handleSaveAvailableEdges() {
    DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> updateRsrc =
        DepanFxWorkspaceResource.forUpdate(
            availableMatchersRsrc, prepareAvailableEdgedResult());
    Dialog<DepanFxLinkMatcherSequenceToolDialog> saveAvailDlg =
        DepanFxLinkMatcherSequenceToolDialog.runCreateDialog(
            updateRsrc, dialogRunner);

    saveAvailDlg.getController().getToolResource()
        .ifPresent(this::updateAvailableEdges);
  }

  private List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      prepareVisibleMatchers() {
    return matcherVisibleProperties.entrySet().stream()
        .filter(e -> e.getValue().get())
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }

  private void updateAvailableEdges(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc) {
    // The resource might have changed in the create dialog.
    setAvailableMatchers(availableMatchersRsrc);

    onAvailableEdgesRsrcUpdate.accept(availableMatchersRsrc);
  }

  private DepanFxLinkMatcherSequenceDocument prepareAvailableEdgedResult() {
    List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> availableMatchers =
        new ArrayList<>(matcherVisibleProperties.size());
    matcherVisibleProperties.keySet().forEach(availableMatchers::add);

    DepanFxLinkMatcherSequenceDocument availableMatchersDoc =
        availableMatchersRsrc.getResource();
    return new DepanFxLinkMatcherSequenceDocument(
        availableMatchersDoc.getToolName(), availableMatchersDoc.getToolDescription(),
        availableMatchersDoc.getModelId(), availableMatchers);
  }
}
