package com.pnambic.depanfx.nodelist.gui.tooldata;

public class DepanFxNodeKeyColumnData extends DepanFxBaseColumnData {

  public static final String NODE_KEY_COLUMN_TOOL_EXT = "dnkcti";

  public static final String BASE_COLUMN_LABEL = "Key";

  public enum KeyChoice { MODEL_KEY, KIND_KEY, NODE_KEY }

  private final KeyChoice keyChoice;

  public DepanFxNodeKeyColumnData(
      String toolName, String toolDescription,
      String columnLabel, int widthMs, KeyChoice keyChoice) {

    super(toolName, toolDescription, columnLabel, widthMs);
    this.keyChoice = keyChoice;
  }

  public KeyChoice getKeyChoice() {
    return keyChoice;
  }
}
