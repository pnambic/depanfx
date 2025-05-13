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

import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData;
import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData.AnnotationSpecification;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry.Contribution;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxBaseColumnToolDialog;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxInfoStoreData;
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
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxInfoRegistry infoRegistry;

  @FXML
  private TextField infoStoreRsrcField;

  @FXML
  private Label infoStoreDescrLabel;

  private DepanFxInfoStoreChooser infoStoreControl;

  @FXML private ComboBox<DepanFxAnnotationIndexData.AnnotationSpecification>
      infoChoiceField;

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
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxInfoRegistry infoRegistry) {
    super(workspace, DepanFxNodeInfoColumnData.class);
    this.dialogRunner = dialogRunner;
    this.infoRegistry = infoRegistry;
  }

  public static Dialog<DepanFxInfoColumnToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc,
      DepanFxDialogRunner dialogRunner) {

    Dialog<DepanFxInfoColumnToolDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            columnRsrc, dialogRunner, DepanFxInfoColumnToolDialog.class);
    result.runDialog(EDIT_INFO_COLUMN_TITLE);
    return result;
  }

  public static Dialog<DepanFxInfoColumnToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc,
      DepanFxDialogRunner dialogRunner) {

    Dialog<DepanFxInfoColumnToolDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            columnRsrc, dialogRunner, DepanFxInfoColumnToolDialog.class);
    result.runDialog(NEW_INFO_COLUMN_TITLE);
    return result;
  }

  @FXML
  public void initialize() {
    infoStoreControl = new DepanFxInfoStoreChooser(
        workspace, dialogRunner, infoStoreRsrcField);
    infoStoreRsrcField.textProperty().addListener(
        (obs, old, upd) -> updateInfoSourceDetails(upd));

    infoChoiceField.setConverter(new ContributionConverter());
    infoChoiceField.valueProperty().addListener(
        (obs, old, upd) -> updateInfoDetails(upd));

    propertyChoiceField.valueProperty().addListener(
        (obs, old, upd) -> updatePropertyDetails(upd));

    updateInfoSourceFields();
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc) {
    super.setToolResource(columnRsrc);

    updateInfoSourceFields();
  }

  @FXML
  public void handleBrowseInfoStore() {
    infoStoreControl.runInfoStoreFinder();
  }

  public static void setNodeKeyColumnTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(INFO_COLUMN_FILTER);
    result.setSelectedExtensionFilter(INFO_COLUMN_FILTER);
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeInfoColumnData prepareResult() {
    return new DepanFxNodeInfoColumnData(
        getToolName(), getToolDescription(),
        getColumnLabel(), getColumnWidthMs(),
        infoStoreControl.getStoreResource(),
        infoChoiceField.getValue().getAnnotationKey(),
        propertyChoiceField.getValue());
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

  private void updateInfoSourceFields() {
    getToolResource()
        .map(r -> r.getResource())
        .ifPresent(i -> {
          infoStoreControl.setInfoStoreResource(i.getInfoSourceResource());
          infoChoiceField.setValue(getInfoChoice(i));
          propertyChoiceField.setValue(i.getInfoProperty());
        });
  }

  private AnnotationSpecification getInfoChoice(DepanFxNodeInfoColumnData i) {
    DepanFxWorkspaceResource<DepanFxInfoStoreData> infoSourceRsrc =
        i.getInfoSourceResource();
    if (infoSourceRsrc != null) {
      return infoSourceRsrc.getResource().getAnnotationIndex()
          .getByAnnotationKey(i.getInfoKey())
          .get();
    }
    return null;
  }

  private void updateInfoSourceDetails(String upd) {
    DepanFxWorkspaceResource<DepanFxInfoStoreData> infoStoreRsrc =
        infoStoreControl.getStoreResource();
    if (infoStoreRsrc != null) {
      DepanFxInfoStoreData infoStore = infoStoreRsrc.getResource();
      infoStoreDescrLabel.setText(infoStore.getToolDescription());
      updateInfoChoices(infoStore);
      return;
    }

    infoStoreDescrLabel.setText(null);
    infoChoiceField.getItems().clear();
  }

  private void updateInfoDetails(AnnotationSpecification upd) {
    if (upd != null) {
      infoDescrLabel.setText(upd.getAnnotationLabel());
      infoDescrLabel.setText(upd.getAnnotationKey());
      updatePropertyChoices(upd);
      return;
    }
    infoDescrLabel.setText(null);
    propertyChoiceField.getItems().clear();
  }

  private void updateInfoChoices(DepanFxInfoStoreData infoStore) {
    ObservableList<AnnotationSpecification> infoItems =
        infoChoiceField.getItems();

    infoItems.clear();
    infoStore.getAnnotationIndex().streamAnnotations()
        .forEach(infoItems::add);
  }

  private void updatePropertyDetails(DepanFxNodeInfoProperty infoProp) {
    if (infoProp != null) {
      propertyDetailsLabel.setText(infoProp.getPropertyKind().toString());
      propertyDescrLabel.setText(infoProp.getToolDescription());
      return;
    }
    propertyDetailsLabel.setText(null);
    propertyDescrLabel.setText(null);
  }

  private void updatePropertyChoices(AnnotationSpecification upd) {
    DepanFxInfoRegistry.Contribution contrib = upd.getAnnotationInfo();
    propertyChoiceField.setConverter(
        new PropertyConverter(contrib));

    ObservableList<DepanFxNodeInfoProperty> propertyItems =
        propertyChoiceField.getItems();
    propertyItems.clear();
    contrib.streamProperties()
        .forEach(propertyItems::add);
  }

  /////////////////////////////////////
  //

  private static class ContributionConverter
      extends StringConverter<DepanFxAnnotationIndexData.AnnotationSpecification> {

    @Override
    public String toString(
        DepanFxAnnotationIndexData.AnnotationSpecification annoDef) {
      return annoDef.getAnnotationLabel();
    }

    @Override
    public DepanFxAnnotationIndexData.AnnotationSpecification fromString(
        String label) {
      return null;
    }
  }

  private static class PropertyConverter
      extends StringConverter<DepanFxNodeInfoProperty> {

    private final DepanFxInfoRegistry.Contribution contrib;

    private PropertyConverter(Contribution contrib) {
      this.contrib = contrib;
    }

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
