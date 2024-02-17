package com.pnambic.depanfx.nodelist.gui.tooldata;

import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

public class DepanFxSimpleColumnData extends DepanFxBaseToolData {

  public static final String SIMPLE_COLUMN_TOOL_EXT = "dscti";

  public static final String BASE_COLUMN_LABEL = "Column";

  private final String columnLabel;

  private final int widthMs;

  public DepanFxSimpleColumnData(
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

