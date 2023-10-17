package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;

public class DepanFxNodeListLeafNode extends DepanFxNodeListMember {

  private final GraphNode node;

  private final DepanFxNodeListSection section;

  public DepanFxNodeListLeafNode(GraphNode node, DepanFxNodeListSection section) {
    this.node = node;
    this.section = section;
  }

  public GraphNode getGraphNode() {
    return node;
  }

  @Override
  public String getDisplayName() {
    return section.getDisplayName(node);
  }

  @Override
  public String getSortKey() {
    return section.getSortKey(node);
  }
}
