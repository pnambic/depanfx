package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;

public class DepanFxNodeListLeafNode extends DepanFxNodeListMember
    implements DepanFxGraphNodeProvider {

  private final GraphNode node;

  private final DepanFxNodeListSection section;

  public DepanFxNodeListLeafNode(GraphNode node, DepanFxNodeListSection section) {
    this.node = node;
    this.section = section;
  }

  @Override
  public String getDisplayName() {
    return section.getDisplayName(node);
  }

  @Override
  public String getSortKey() {
    return section.getSortKey(node);
  }

  @Override // DepanFxGraphNodeProvider
  public GraphNode getGraphNode() {
    return node;
  }
}
