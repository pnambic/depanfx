package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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
  public void setTooldata(T toolData) {
    toolNameField.setText(toolData.getToolName());
    toolDescriptionField.setText(toolData.getToolDescription());
  }

  protected String getToolName() {
    return toolNameField.getText();
  }

  protected void updateBlankToolName(String newValue) {
    updateBlankField(toolNameField, newValue);
  }

  protected String getToolDescription() {
    return toolDescriptionField.getText();
  }

  protected void updateBlankToolDescription(String newValue) {
    updateBlankField(toolDescriptionField, newValue);
  }

  @Override
  protected String getDocumentName() {
    return getToolName();
  }
}
