package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

public class DepanFxRadialLayoutData extends DepanFxBaseToolData {

  public static final String RADIAL_LAYOUT_TOOL_EXT = "drlti";

  private final DepanFxWorkspaceResource linkMatcherRsrc;

  public DepanFxRadialLayoutData(
      String toolName, String toolDescription,
      DepanFxWorkspaceResource linkMatcherRsrc) {
    super(toolName, toolDescription);
    this.linkMatcherRsrc = linkMatcherRsrc;
  }

  public DepanFxWorkspaceResource getHierarchyMatcherRsrc() {
    return linkMatcherRsrc;
  }
}
