package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

public class DepanFxRadialLayoutData extends DepanFxBaseToolData {

  public static final String RADIAL_LAYOUT_TOOL_EXT = "drlti";

  private final DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>
      linkMatcherRsrc;

  public DepanFxRadialLayoutData(
      String toolName, String toolDescription,
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> linkMatcherRsrc) {
    super(toolName, toolDescription);
    this.linkMatcherRsrc = linkMatcherRsrc;
  }

  public DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>
      getHierarchyMatcherRsrc() {
    return linkMatcherRsrc;
  }
}
