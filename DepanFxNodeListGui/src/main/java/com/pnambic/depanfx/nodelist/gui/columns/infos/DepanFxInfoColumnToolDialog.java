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

import com.pnambic.depanfx.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxBaseColumnToolDialog;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;

@DepanFxFxmlDialog
@FxmlView("info-column-tool-dialog.fxml")
public class DepanFxInfoColumnToolDialog
    extends DepanFxBaseColumnToolDialog<DepanFxNodeInfoColumnData> {

  public static final ExtensionFilter INFO_COLUMN_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Info Columns", DepanFxNodeInfoColumnData.NODE_INFO_COLUMN_TOOL_EXT);

  public static final DepanFxResourceFilter INFO_COLUMN_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Info Columns", DepanFxNodeInfoColumnData.NODE_INFO_COLUMN_TOOL_EXT,
          DepanFxNodeInfoColumnData.class);

  public static final String EDIT_INFO_COLUMN_TITLE =
      "Edit Info Column";

  public static final String NEW_INFO_COLUMN_TITLE =
      "New Info Column";

  private final DepanFxInfoRegistry infoRegistry;

  @FXML
  private ComboBox<DepanFxInfoRegistry.Contribution> infoChoiceField;

  @FXML
  private Label infoDescrLabel;

  @FXML
  private ComboBox<DepanFxNodeInfoProperty> propertyChoiceField;

  @FXML
  private Label propertyDetailsLabel;

  @FXML
  private Label propertyDescrLabel;

  @Autowired
  public DepanFxInfoColumnToolDialog(
      DepanFxWorkspace workspace, DepanFxInfoRegistry infoRegistry) {
    super(workspace, DepanFxNodeInfoColumnData.class);
    this.infoRegistry = infoRegistry;
  }

  public static Dialog<DepanFxInfoColumnToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        columnRsrc, dialogRunner,
        DepanFxInfoColumnToolDialog.class,
        EDIT_INFO_COLUMN_TITLE);
  }

  public static Dialog<DepanFxInfoColumnToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        columnRsrc, dialogRunner,
        DepanFxInfoColumnToolDialog.class,
        NEW_INFO_COLUMN_TITLE);
  }

  public static void setNodeKeyColumnTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(INFO_COLUMN_FILTER);
    result.setSelectedExtensionFilter(INFO_COLUMN_FILTER);
  }

  @FXML
  public void initialize() {
    ObservableList<DepanFxInfoRegistry.Contribution> infoItems =
        infoChoiceField.getItems();
    infoRegistry.streamContributions()
        .forEach(infoItems::add);
    infoChoiceField.setConverter(new ContributionConverter());
    infoChoiceField.setOnAction(e -> handleInfoAction(e));

    propertyChoiceField.setConverter(new PropertyConverter());
    propertyChoiceField.setOnAction(e -> handlePropertyAction(e));
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc) {
    super.setToolResource(columnRsrc);

    DepanFxNodeInfoColumnData columnInfo = columnRsrc.getResource();
    infoChoiceField.setValue(columnInfo.getInfoContribution());
    propertyChoiceField.setValue(columnInfo.getInfoProperty());
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeInfoColumnData prepareResult() {
    DepanFxInfoRegistry.Contribution info = infoChoiceField.getValue();
    DepanFxNodeInfoProperty property = propertyChoiceField.getValue();

    return new DepanFxNodeInfoColumnData(
        getToolName(), getToolDescription(),
        getColumnLabel(), getColumnWidthMs(),
        info, property);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeInfoColumnData.NODE_INFO_COLUMN_TOOL_EXT,
        DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxInfoColumnToolDialog.setNodeKeyColumnTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Infos Column Save Confirmation Error";
  }

  private void handleInfoAction(ActionEvent event) {
    DepanFxInfoRegistry.Contribution infoContrib = infoChoiceField.getValue();
    infoDescrLabel.setText(infoContrib.getInfoDescription());

    ObservableList<DepanFxNodeInfoProperty> propertyItems =
        propertyChoiceField.getItems();
    propertyItems.clear();
    infoContrib.streamProperties()
        .forEach(propertyItems::add);
  }

  private void handlePropertyAction(ActionEvent event) {
    DepanFxNodeInfoProperty infoProp = propertyChoiceField.getValue();
    if (infoProp != null) {
      propertyDetailsLabel.setText(infoProp.getPropertyKind().toString());
      propertyDescrLabel.setText(infoProp.getToolDescription());
      return;
    }
    propertyDetailsLabel.setText(null);
    propertyDescrLabel.setText(null);
  }

  /////////////////////////////////////
  //

  private class ContributionConverter
      extends StringConverter<DepanFxInfoRegistry.Contribution> {

    @Override
    public String toString(DepanFxInfoRegistry.Contribution contribution) {
        return contribution.getInfoLabel();
    }

    @Override
    public DepanFxInfoRegistry.Contribution fromString(String label) {
      return infoRegistry.streamByLabel(label)
          .findFirst()
          .orElse(null);
    }
  }

  private class PropertyConverter
      extends StringConverter<DepanFxNodeInfoProperty> {

    DepanFxInfoRegistry.Contribution contrib;

    @Override
    public String toString(DepanFxNodeInfoProperty contribution) {
        return contribution.getToolName();
    }

    @Override
    public DepanFxNodeInfoProperty fromString(String label) {
      return contrib.getProperty(label).orElse(null);
    }
  }
}
