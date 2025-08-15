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
package com.pnambic.depanfx.edgematchers.gui;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxActionTableCell;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("matcher-sequence-tool-dialog.fxml")
public class DepanFxLinkMatcherSequenceToolDialog
    extends DepanFxBaseToolDialog<DepanFxLinkMatcherSequenceDocument> {

  @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxLinkMatcherSequenceToolDialog.class);

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

  private final DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry;

  @FXML
  private Label linkMatchersLabel;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
      linkMatcherSequenceTable;

  private ObservableList<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
      linkMatcherSequenceTableData;

  @Autowired
  public DepanFxLinkMatcherSequenceToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
      DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry) {
    super(workspace, DepanFxLinkMatcherSequenceDocument.class);
    this.dialogRunner = dialogRunner;
    this.matcherDialogRegistry = matcherDialogRegistry;
  }

  public static Dialog<DepanFxLinkMatcherSequenceToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> matcherSeqRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        matcherSeqRsrc, dialogRunner,
        DepanFxLinkMatcherSequenceToolDialog.class,
        EDIT_LINK_MATCHER_SEQUENCE);
  }

  public static Dialog<DepanFxLinkMatcherSequenceToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> matcherSeqRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        matcherSeqRsrc, dialogRunner,
        DepanFxLinkMatcherSequenceToolDialog.class,
        CREATE_LINK_MATCHER_SEQUENCE);
  }

  public static void setLinkMatcherSequenceTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(LINK_MATCHER_SEQUENCE_FILTER);
    result.setSelectedExtensionFilter(LINK_MATCHER_SEQUENCE_FILTER);
  }

  @FXML
  public void initialize() {
    linkMatchersLabel.setContextMenu(buildLinkMatchersMenu());
    linkMatcherSequenceTable.setContextMenu(buildMatcherTableMenu());

    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
    columnBinder = new DepanFxTableColumnBinder<>(linkMatcherSequenceTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>, String>
    resourceColumn = columnBinder.next();
    resourceColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            DepanFxProjects.asReferenceLabel(workspace, r.getValue())));

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>, String>
    filePathColumn = columnBinder.next();
    filePathColumn.setCellFactory(c ->
        new DepanFxLinkMatcherChooser.LinkMatcherCell<>(
        getWorkspace(), dialogRunner, getScene(), matcherDialogRegistry,
        (t, r) -> updateMatcher(t, r)));

    filePathColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getResource().getToolName()));

    TableColumn<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>, String>
    rowActionColumn = columnBinder.next();
    DepanFxActionTableCell.prepareColumn(rowActionColumn, p -> new MatcherActions());

    // Size the resource column to remaining room
    resourceColumn.prefWidthProperty().bind(
        linkMatcherSequenceTable.widthProperty()
            .subtract(filePathColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(2));
  }

  private void updateMatcher(
      TableRow<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> t,
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> r) {
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> matcherSeqRsrc) {
    super.setToolResource(matcherSeqRsrc);

    linkMatcherSequenceTableData =
        FXCollections.observableArrayList();
    matcherSeqRsrc.getResource().streamMatchers()
        .forEach(linkMatcherSequenceTableData::add);
    linkMatcherSequenceTable.setItems(linkMatcherSequenceTableData);
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxLinkMatcherSequenceDocument prepareResult() {
    // Ensure the use of a serializable ArrayList.
    List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> matchers =
        new ArrayList<>(linkMatcherSequenceTableData.size());
    linkMatcherSequenceTableData.forEach(matchers::add);

    return new DepanFxLinkMatcherSequenceDocument(
            getToolName(), getToolDescription(), matchers);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_EXT,
        DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxLinkMatcherSequenceToolDialog.setLinkMatcherSequenceTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Link Matcher Sequence Save Confirmation Error";
  }

  /////////////////////////////////////
  // Internal Table Classes

  @FXML
  private void addLinkMatcher() {
    DepanFxLinkMatcherChooser.runLinkMatcherFinder(
            getWorkspace(), dialogRunner, getScene(), matcherDialogRegistry)
        .ifPresent(linkMatcherSequenceTableData::add);
  }

  private ContextMenu buildLinkMatchersMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Add Link Matcher...", e -> addLinkMatcher());
    return builder.build();
  }

  private ContextMenu buildMatcherTableMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Add Link Matcher...", e -> addLinkMatcher());
    builder.appendSeparator();
    /// edgeFiltersDialogRegistry.appendAddFilters(builder);

    builder.appendActionItem("Select Link Matcher...",
        e -> runMatcherChooser(
                 linkMatcherSequenceTable.getSelectionModel()
            .getSelectedIndex()));
    return builder.build();
  }

  private void runMatcherChooser(int selectedIndex) {
    // TODO Auto-generated method stub
  }

  private class MatcherActions
      extends DepanFxActionTableCell<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> {

    public MatcherActions() {
      super(linkMatcherSequenceTableData);
    }

    @Override
    protected void populateContextMenu(DepanFxContextMenuBuilder builder) {
      builder.appendActionItem("Select Matcher...",
          e -> runMatcherChooser(getIndex()));
      appendMoveOps(builder);
    }

    private void runMatcherChooser(int index) {
      DepanFxLinkMatcherChooser.runLinkMatcherFinder(
          getWorkspace(), dialogRunner, getScene(), matcherDialogRegistry)
          .ifPresent(r -> setRow(index, r));
    }
  }
}
