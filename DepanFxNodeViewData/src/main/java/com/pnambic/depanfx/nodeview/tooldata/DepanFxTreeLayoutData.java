package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

public class DepanFxTreeLayoutData extends DepanFxBaseToolData {

  public static final String TREE_LAYOUT_TOOL_EXT = "dtlti";

  private final DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>
      linkMatcherRsrc;

  public DepanFxTreeLayoutData(
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
