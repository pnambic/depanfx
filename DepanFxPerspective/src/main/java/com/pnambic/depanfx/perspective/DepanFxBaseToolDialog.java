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
package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;


/*
 * Extends base tool's type to include support for a tool name
 * and a tool description field.
 *
 * Completing the abstract methods commits the derived dialogs to
 * managing tool names and descriptions in a uniform manner.
 */
public abstract class DepanFxBaseToolDialog<T extends DepanFxBaseToolData>
    extends DepanFxBaseDocumentDialog<T> {

  @FXML
  private TextField toolNameField;

  @FXML
  private TextField toolDescriptionField;

  public DepanFxBaseToolDialog(DepanFxWorkspace workspace, Class<T> dataType) {
    super(workspace, dataType);
  }

  /////////////////////////////////////
  // Hook methods for derived classes.

  /**
   * Extendible, {@code @Override} with {@code super.setTooldata()}.
   */
  @Override
  public void setToolResource(DepanFxWorkspaceResource<T> toolRsrc) {
    super.setToolResource(toolRsrc);

    T toolData = toolRsrc.getResource();
    toolNameField.setText(toolData.getToolName());
    toolDescriptionField.setText(toolData.getToolDescription());
  }

  protected String getToolName() {
    return toolNameField.getText();
  }

  protected void setToolName(String newValue) {
    toolNameField.setText(newValue);
  }

  protected void updateBlankToolName(String newValue) {
    updateBlankField(toolNameField, newValue);
  }

  protected String getToolDescription() {
    return toolDescriptionField.getText();
  }

  protected void setToolDescription(String newValue) {
    toolDescriptionField.setText(newValue);
  }

  protected void updateBlankToolDescription(String newValue) {
    updateBlankField(toolDescriptionField, newValue);
  }

  @Override
  protected String getDocumentName() {
    return getToolName();
  }
}
