package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

public class DepanFxBaseColumnData extends DepanFxBaseToolData {

  public static final String BASE_COLUMN_NAME = "Column";

  public static final int BASE_COLUMN_WIDTH_MS = 12;

  private final String columnLabel;

  private final int widthMs;

  public DepanFxBaseColumnData(
      String toolName, String toolDescription,
      String columnLabel, int widthMs) {

    super(toolName, toolDescription);
    this.columnLabel = columnLabel;
    this.widthMs = widthMs;
  }

  public String getColumnLabel() {
    return columnLabel;
  }

  public int getWidthMs() {
    return widthMs;
  }
}
