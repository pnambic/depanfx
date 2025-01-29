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
package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxShiftLayoutData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("shift-layout-tool-dialog.fxml")
public class DepanFxShiftLayoutToolDialog
    extends DepanFxBaseToolDialog<DepanFxShiftLayoutData> {

  public static final ExtensionFilter SHIFT_LAYOUT_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Shift Layout", DepanFxShiftLayoutData.SHIFT_LAYOUT_TOOL_EXT);

  public static final DepanFxResourceFilter SHIFT_LAYOUT_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Shift Layout", DepanFxShiftLayoutData.SHIFT_LAYOUT_TOOL_EXT,
          DepanFxShiftLayoutData.class);

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxShiftLayoutToolDialog.class);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField shiftXField;

  @FXML
  private TextField shiftYField;

  @FXML
  private TextField shiftZField;

  @Autowired
  public DepanFxShiftLayoutToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxShiftLayoutData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxShiftLayoutToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxShiftLayoutData> shiftLayoutRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        shiftLayoutRsrc, dialogRunner,
        DepanFxShiftLayoutToolDialog.class,
        "Edit Shift Layout");
  }

  public static Dialog<DepanFxShiftLayoutToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxShiftLayoutData> shiftLayoutRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        shiftLayoutRsrc, dialogRunner,
        DepanFxShiftLayoutToolDialog.class,
        "New Shift Layout");
  }

  @FXML
  public void initialize() {
    configureShiftField(shiftXField);
    configureShiftField(shiftYField);
    configureShiftField(shiftZField);
  }

  
  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxShiftLayoutData> toolRsrc) {
    super.setToolResource(toolRsrc);

    DepanFxShiftLayoutData shiftLayoutData = toolRsrc.getResource();
    setShiftField(shiftXField, shiftLayoutData.getShiftX());
    setShiftField(shiftYField, shiftLayoutData.getShiftY());
    setShiftField(shiftZField, shiftLayoutData.getShiftZ());
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxShiftLayoutData prepareResult() {
    return new DepanFxShiftLayoutData(
        getToolName(), getToolDescription(),
        parseShiftField(shiftXField),
        parseShiftField(shiftYField),
        parseShiftField(shiftZField));
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxShiftLayoutData.SHIFT_LAYOUT_TOOL_EXT,
        DepanFxNodeViewLayoutData.LAYOUT_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(SHIFT_LAYOUT_FILTER);
    result.setSelectedExtensionFilter(SHIFT_LAYOUT_FILTER);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Shift Layout Save Confirmation Error";
  }

  private void setShiftField(TextField shiftField, double shift) {
    shiftField.setText(String.valueOf(shift));
  }

  private void configureShiftField(TextField field) {
    field.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!newValue.matches("-?\\d*")) { // Allow negative or positive integers
            field.setText(oldValue);
        }
    });
  }

  private double parseShiftField(TextField shiftField) {
    String shiftText = shiftField.getText();
    try {
      return Double.parseDouble(shiftText);
  } catch (NumberFormatException e) {
    LOG.warn("Bad user value for shift amount {}", shiftField.getText());
  }
    return 0;
  }
}
