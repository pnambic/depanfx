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
package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxActionTableCell;
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

  @FXML
  private Label linkMatchersLabel;

  @FXML
  private TableView<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      linkMatcherSequenceTable;

  private ObservableList<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      linkMatcherSequenceTableData;

  // Retain for internal properties, like graph model.
  //private DepanFxLinkMatcherSequenceDocument matcherSeqDoc;

  @Autowired
  public DepanFxLinkMatcherSequenceToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxLinkMatcherSequenceDocument.class);
    this.dialogRunner = dialogRunner;
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

    DepanFxTableColumnBinder<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
    columnBinder = new DepanFxTableColumnBinder<>(linkMatcherSequenceTable);

    TableColumn<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, String>
    resourceColumn = columnBinder.next();
    resourceColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getDocument().getMemberPath().toString()));

    TableColumn<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, String>
    filePathColumn = columnBinder.next();
    filePathColumn.setCellValueFactory(
        r -> new SimpleStringProperty(
            r.getValue().getResource().getToolName()));

    TableColumn<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, String>
    rowActionColumn = columnBinder.next();
    DepanFxActionTableCell.prepareColumn(rowActionColumn, p -> new MatcherActions());

    // Size the resource column to remaining room
    resourceColumn.prefWidthProperty().bind(
        linkMatcherSequenceTable.widthProperty()
            .subtract(filePathColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(2));
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
    List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> matchers =
        new ArrayList<>(linkMatcherSequenceTableData.size());
    linkMatcherSequenceTableData.forEach(matchers::add);

    DepanFxLinkMatcherSequenceDocument matchSeqInfo =
        getToolResource().get().getResource();
    return new DepanFxLinkMatcherSequenceDocument(
            getToolName(), getToolDescription(),
            matchSeqInfo.getModelId(), matchers);
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
            getWorkspace(), dialogRunner, getScene())
        .ifPresent(linkMatcherSequenceTableData::add);
  }

  private ContextMenu buildLinkMatchersMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Add Link Matcher...", e -> addLinkMatcher());
    return builder.build();
  }

  private class MatcherActions
      extends DepanFxActionTableCell<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> {

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
          getWorkspace(), dialogRunner, getScene())
          .ifPresent(r -> setRow(index, r));
    }
  }
}
