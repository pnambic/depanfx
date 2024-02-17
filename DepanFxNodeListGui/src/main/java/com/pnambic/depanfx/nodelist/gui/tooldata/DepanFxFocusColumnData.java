package com.pnambic.depanfx.nodelist.gui.tooldata;

import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

public class DepanFxFocusColumnData extends DepanFxBaseColumnData {

  public static final String FOCUS_COLUMN_TOOL_EXT = "dfcti";

  private static final String BASE_COLUMN_DESCR = "New focus column.";

  public static final String BASE_COLUMN_LABEL = "Focus";

  private static final String BASE_FOCUS_LABEL = null;

  private final String focusLabel;

  private final DepanFxWorkspaceResource nodeListRsrc;

  public DepanFxFocusColumnData(
      String toolName, String toolDescription,
      String columnLabel, int widthMs,
      String focusLabel, DepanFxWorkspaceResource nodeListRsrc) {

    super(toolName, toolDescription, columnLabel, widthMs);
    this.focusLabel = focusLabel;
    this.nodeListRsrc = nodeListRsrc;
  }

  public static DepanFxFocusColumnData buildInitialFocusColumnData(
      DepanFxWorkspaceResource nodeListRsrc) {
    return new DepanFxFocusColumnData(
        DepanFxBaseColumnData.BASE_COLUMN_NAME, BASE_COLUMN_DESCR,
        BASE_COLUMN_LABEL, DepanFxBaseColumnData.BASE_COLUMN_WIDTH_MS,
        BASE_FOCUS_LABEL, nodeListRsrc);
  }

  public String getFocusLabel() {
    return focusLabel;
  }

  public DepanFxWorkspaceResource getNodeListRsrc() {
    return nodeListRsrc;
  }
}
