package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.model.GraphNode;

public class DepanFxNodeListLeafNode extends DepanFxNodeListMember {

  private final GraphNode node;

  public DepanFxNodeListLeafNode(GraphNode node) {
    this.node = node;
  }

  public GraphNode getGraphNode() {
    return node;
  }

  @Override
  public String getMemberName() {
    return node.getId().getNodeKey();
  }
}
