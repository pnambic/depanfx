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
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
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

@DepanFxFxmlDialog
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

  private Map<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, BooleanProperty>
      matcherVisibleProperties;

  private EdgeDisplayController edgeDisplay;

  @Autowired
  public DepanFxNodeViewEdgeVisibilityDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxLinkMatcherSequenceDocument.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxNodeViewEdgeVisibilityDialog> runVisibilityDialog(
      DepanFxDialogRunner dialogRunner, EdgeDisplayController edgeDisplay) {

    Dialog<DepanFxNodeViewEdgeVisibilityDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            edgeDisplay.forUpdateVisibleMatcherSequenceDoc(), dialogRunner,
            DepanFxNodeViewEdgeVisibilityDialog.class);

    DepanFxNodeViewEdgeVisibilityDialog dlgCtrl = result.getController();
    dlgCtrl.setEdgeDisplayController(edgeDisplay);
    result.runDialog(CREATE_LINK_MATCHER_SEQUENCE);
    return result;
  }

  /**
   * This should be done first, and once.
   */
  private void setEdgeDisplayController(EdgeDisplayController edgeDisplay) {
    this.edgeDisplay = edgeDisplay;
    installAvailableMatchers();
  }

  @FXML
  public void initialize() {
    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
    columnBinder = new DepanFxTableColumnBinder<>(matcherVisibilityTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, String>
    matcherNameColumn = columnBinder.next();
    matcherNameColumn.setCellValueFactory(
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
            edgeDisplay.getVisiblityMatcherEdgeCount(r.getValue())));

    // Size filePath to remaining room
    matcherNameColumn.prefWidthProperty().bind(
        matcherVisibilityTable.widthProperty()
            .subtract(isVisibleColumn.widthProperty())
            .subtract(countColumn.widthProperty())
            .subtract(1));
  }

  /**
   * For visibility dialog, tool data handles the visible matchers,
   * and the available matchers are handled separately.
   */

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleMatcherRsrc) {
    super.setToolResource(visibleMatcherRsrc);

    // The life cycle of DepanFxBaseToolDialog with
    // DepanFxResourcePerspectives.prepareDialog() invokes this method
    // before the edgeDisplay is connected for updates.
    if (edgeDisplay != null) {
      edgeDisplay.setVisibiltyResource(visibleMatcherRsrc);

      installVisibleMatchers();
    }
  }

  public void setAvailableMatchers(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc) {
    edgeDisplay.updateAvailableMatchers(availableMatchersRsrc);
    installAvailableMatchers();
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxLinkMatcherSequenceDocument prepareResult() {
    // Build results from UX table, not current nodeDisplay.
    // .. even though these should be the same if live updates are working.
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
  // Visibility properties and updates

  private void installFilterProperty(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    BooleanProperty visibilityProp = new SimpleBooleanProperty();
    visibilityProp.addListener((e, o, n) -> updateVisibility(matcherRsrc, n));
    matcherVisibleProperties.put(matcherRsrc, visibilityProp);
  }

  /**
   * Bring the matcher visibility properties up to date with the latest
   * edge display, and indirectly the matcher visibility table that listens
   * to those properties.
   */
  private void installVisibleMatchers() {
    if (getToolResource() == null || edgeDisplay == null) {
      return;
    }

    edgeDisplay.forEachAvailableMatchers(
        r -> matcherVisibleProperties.get(r).set(
            edgeDisplay.getMatcherVisibility(r)));
  }

  private void updateVisibility(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc,
      Boolean visibility) {
    edgeDisplay.setMatcherVisibility(matcherRsrc, visibility);
  }

  private void installAvailableMatchers() {
    matcherVisibleProperties = new HashMap<>();
    edgeDisplay.forEachAvailableMatchers(this::installFilterProperty);

    installVisibleMatchers();

    ObservableList<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
    availableMatchers = FXCollections.observableArrayList();

    edgeDisplay.forEachAvailableMatchers(availableMatchers::add);

    matcherVisibilityData = availableMatchers;
    matcherVisibilityTable.setItems(availableMatchers);
  }

  /////////////////////////////////////
  // Context menu handlers

  @FXML
  protected void handleRevert() {
    handleCancel();
    edgeDisplay.revertLinkDisplay();
  }

  @FXML
  private void handleApply() {
    edgeDisplay.setVisibiltyResource(
        DepanFxWorkspaceResource.forUpdate(
            getToolResource().get(), prepareResult()));
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
    DepanFxLinkMatcherSequenceToolDialog.runCreateDialog(
        edgeDisplay.forUpdateAvailableMatcherSequenceDoc(), dialogRunner)
        .getController()
        .getToolResource()
        .ifPresent(this::updateAvailableEdges);
  }

  /**
   * Provides an alphabetically ordered sequence of matcher resources,
   * based on the tool name of each matcher.
   *
   * This ensure that consumers always see the same order,
   * regardless of set construction.
   */
  private List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      prepareVisibleMatchers() {
    return matcherVisibleProperties.entrySet().stream()
        .filter(e -> e.getValue().get())
        .map(e -> e.getKey())
        .sorted(DepanFxBaseToolData.BY_RESOURCE_NAME)
        .collect(Collectors.toList());
  }

  private void updateAvailableEdges(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc) {
    // The resource might have changed in the create dialog.
    edgeDisplay.updateAvailableMatchers(availableMatchersRsrc);
    setAvailableMatchers(availableMatchersRsrc);
  }
}
