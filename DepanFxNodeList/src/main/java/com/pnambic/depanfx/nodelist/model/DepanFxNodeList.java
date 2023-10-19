package com.pnambic.depanfx.nodelist.model;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;

public class DepanFxNodeList {

  private final DepanFxWorkspaceResource wkspRsrc;

  private final Collection<GraphNode> nodes;

  public DepanFxNodeList(
      DepanFxWorkspaceResource wkspRsrc,
      Collection<GraphNode> nodes) {
    this.wkspRsrc = wkspRsrc;
    this.nodes = nodes;
  }

  public DepanFxWorkspaceResource getWorkspaceResource() {
    return wkspRsrc;
  }

  public Collection<GraphNode> getNodes() {
    return nodes;
  }

}
