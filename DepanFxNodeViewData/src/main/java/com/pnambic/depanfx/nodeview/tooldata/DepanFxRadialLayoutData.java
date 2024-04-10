package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

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
