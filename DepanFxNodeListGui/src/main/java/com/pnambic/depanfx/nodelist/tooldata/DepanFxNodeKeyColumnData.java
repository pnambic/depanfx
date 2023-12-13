package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

public class DepanFxNodeKeyColumnData extends DepanFxBaseToolData {

  public static final String NODE_KEY_COLUMN_TOOL_EXT = "dnkcti";

  public static final String BASE_COLUMN_LABEL = "Key";

  public enum KeyChoice { MODEL_KEY, KIND_KEY, NODE_KEY }

  private final String columnLabel;

  private final KeyChoice keyChoice;

  private final int widthMs;

  public DepanFxNodeKeyColumnData(
      String toolName, String toolDescription,
      String columnLabel, int widthMs, KeyChoice keyChoice) {

    super(toolName, toolDescription);
    this.columnLabel = columnLabel;
    this.widthMs = widthMs;
    this.keyChoice = keyChoice;
  }

  public String getColumnLabel() {
    return columnLabel;
  }

  public KeyChoice getKeyChoice() {
    return keyChoice;
  }

  public int getWidthMs() {
    return widthMs;
  }
}

