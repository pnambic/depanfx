package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public abstract class DepanFxBaseColumnToolDialog<T extends DepanFxBaseColumnData>
    extends DepanFxBaseToolDialog<T> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxBaseColumnToolDialog.class);

  public static final int BASE_COLUMN_WIDTH = 10;

  public static final int MAX_COLUMN_WIDTH = 200;

  public static final int MIN_COLUMN_WIDTH = 5;

  @FXML
  protected TextField columnLabelField;

  @FXML
  protected TextField widthMsField;

  public DepanFxBaseColumnToolDialog(
      DepanFxWorkspace workspace, Class<T> forType) {
    super(workspace, forType);
  }

  /**
   * Extendible, {@code @Override} with {@code super.setTooldata()}.
   */
  public void setTooldata(T toolData) {
    super.setTooldata(toolData);
    toolNameField.setText(toolData.getToolName());
    toolDescriptionField.setText(toolData.getToolDescription());
  }

  /////////////////////////////////////
  // Hook methods for derived classes.

  /**
   * Available to implement {@link #prepareResult()}.
   */
  protected int parseWidthMs(String widthMs) {
    int result = BASE_COLUMN_WIDTH;
    try {
      result = Integer.parseUnsignedInt(widthMs);
    } catch (NumberFormatException errFmt) {
      LOG.warn("Bad user value for widthMs {}", widthMs, errFmt);
    }
    return Math.min(MAX_COLUMN_WIDTH, Math.max(MIN_COLUMN_WIDTH, result));
  }
}
