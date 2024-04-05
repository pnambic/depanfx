package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

public class DepanFxTreeLayoutData extends DepanFxBaseToolData {

  public static final String TREE_LAYOUT_TOOL_EXT = "dtlti";

  private final DepanFxWorkspaceResource linkMatcherRsrc;

  public DepanFxTreeLayoutData(
      String toolName, String toolDescription,
      DepanFxWorkspaceResource linkMatcherRsrc) {
    super(toolName, toolDescription);
    this.linkMatcherRsrc = linkMatcherRsrc;
  }

  public DepanFxWorkspaceResource getHierarchyMatcherRsrc() {
    return linkMatcherRsrc;
  }
}
