package com.pnambic.depanfx.nodelist.model;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;

public class DepanFxNodeList {

  /** Common extension for any workspace persisted node list. */
  public static final String NODE_LIST_EXT = "dnli";

  private final String nodeListName;

  private final String nodeListDescription;

  private final DepanFxWorkspaceResource graphDocRsrc;

  private final Collection<GraphNode> nodes;

  public DepanFxNodeList(
      String nodeListName,
      String nodeListDescription,
      DepanFxWorkspaceResource graphDocRsrc,
      Collection<GraphNode> nodes) {
    this.nodeListName = nodeListName;
    this.nodeListDescription = nodeListDescription;
    this.graphDocRsrc = graphDocRsrc;
    this.nodes = nodes;
  }

  public String getNodeListName() {
    return nodeListName;
  }

  public String getNodeListDescription() {
    return nodeListDescription;
  }

  public DepanFxWorkspaceResource getGraphDocResource() {
    return graphDocRsrc;
  }

  public Collection<GraphNode> getNodes() {
    return nodes;
  }
}
