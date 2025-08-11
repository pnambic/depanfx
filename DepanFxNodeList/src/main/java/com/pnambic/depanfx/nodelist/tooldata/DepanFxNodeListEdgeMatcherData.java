package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

/**
 * Tool data for link matchers that are based on a pair of node lists.
 *
 * <p>A {@code null} value for either node list indicates no constraint
 * on that end of the edge.
 *
 * <p>An empty list of node for either node list also indicates no constraint
 * on that end of the edge.
 *
 * <p>These interpretive semantics are implemented in the
 * {@code DepanFxNodeListEdgeMatchers#createMatcher()} method.
 */
public class DepanFxNodeListEdgeMatcherData extends DepanFxBaseMatcherDocument {

  public static final String NODE_LIST_EDGE_MATCHER_TOOL_EXT = "dnlmti";

  private final DepanFxWorkspaceResource<DepanFxNodeList> headNodesRsrc;

  private final DepanFxWorkspaceResource<DepanFxNodeList> tailNodesRsrc;

  public DepanFxNodeListEdgeMatcherData(
      String toolName,
      String toolDescription,
      DepanFxWorkspaceResource<DepanFxNodeList> headNodesRsrc,
      DepanFxWorkspaceResource<DepanFxNodeList> tailNodesRsrc) {
    super(toolName, toolDescription);
    this.headNodesRsrc = headNodesRsrc;
    this.tailNodesRsrc = tailNodesRsrc;
  }

  /** May return a{@code null} value if no head nodes are defined. */
  public DepanFxWorkspaceResource<DepanFxNodeList> getHeadNodesResource() {
    return headNodesRsrc;
  }

  /** May return a {@code null} value if no tail nodes are defined. */
  public DepanFxWorkspaceResource<DepanFxNodeList> getTailNodesResource() {
    return tailNodesRsrc;
  }
}
