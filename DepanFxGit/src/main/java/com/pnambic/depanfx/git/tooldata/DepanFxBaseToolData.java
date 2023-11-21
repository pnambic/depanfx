package com.pnambic.depanfx.git.tooldata;

/**
 * The minimal expectations for any tool data.
 */
public class DepanFxBaseToolData {

  private final String toolName;

  private final String toolDescription;

  public DepanFxBaseToolData(String toolName, String toolDescription) {
    this.toolName = toolName;
    this.toolDescription = toolDescription;
  }

  public String getToolName() {
    return toolName;
  }

  public String getToolDescription() {
    return toolDescription;
  }
}
