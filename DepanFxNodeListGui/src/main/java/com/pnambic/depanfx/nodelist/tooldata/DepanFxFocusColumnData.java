package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

public class DepanFxFocusColumnData extends DepanFxBaseToolData {

  public static final String FOCUS_COLUMN_TOOL_EXT = "dfcti";

  private static final String BASE_COLUMN_NAME = "Column";

  private static final String BASE_COLUMN_DESCR = "New focus column.";

  public static final String BASE_COLUMN_LABEL = "Focus";

  public static final int BASE_COLUMN_WIDTH_MS = 12;

  private static final String BASE_FOCUS_LABEL = null;

  private final String columnLabel;

  private final int widthMs;

  private final String focusLabel;

  private final DepanFxWorkspaceResource nodeListRsrc;

  public DepanFxFocusColumnData(
      String toolName, String toolDescription,
      String columnLabel, int widthMs,
      String focusLabel, DepanFxWorkspaceResource nodeListRsrc) {

    super(toolName, toolDescription);
    this.columnLabel = columnLabel;
    this.widthMs = widthMs;
    this.focusLabel = focusLabel;
    this.nodeListRsrc = nodeListRsrc;
  }

  public static DepanFxFocusColumnData buildInitialFocusColumnData(
      DepanFxWorkspaceResource nodeListRsrc) {
    return new DepanFxFocusColumnData(
        BASE_COLUMN_NAME, BASE_COLUMN_DESCR,
        BASE_COLUMN_LABEL, BASE_COLUMN_WIDTH_MS,
        BASE_FOCUS_LABEL, nodeListRsrc);
  }

  public String getColumnLabel() {
    return columnLabel;
  }

  public int getWidthMs() {
    return widthMs;
  }

  public String getFocusLabel() {
    return focusLabel;
  }

  public DepanFxWorkspaceResource getNodeListRsrc() {
    return nodeListRsrc;
  }
}
