package com.pnambic.depanfx.nodelist.model;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;

public class DepanFxNodeList {

  /** Common extension for any workspace persisted node list. */
  public static final String NODE_LIST_EXT = "dnli";

  private final DepanFxWorkspaceResource graphDocRsrc;

  private final Collection<GraphNode> nodes;

  public DepanFxNodeList(
      DepanFxWorkspaceResource graphDocRsrc,
      Collection<GraphNode> nodes) {
    this.graphDocRsrc = graphDocRsrc;
    this.nodes = nodes;
  }

  public DepanFxWorkspaceResource getGraphDocResource() {
    return graphDocRsrc;
  }

  public Collection<GraphNode> getNodes() {
    return nodes;
  }
}
