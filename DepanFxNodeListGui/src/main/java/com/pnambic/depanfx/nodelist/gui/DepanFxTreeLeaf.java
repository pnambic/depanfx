package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;

public class DepanFxTreeLeaf extends DepanFxNodeListMember {

  private final GraphNode node;

  private final DepanFxTreeSection section;

  public DepanFxTreeLeaf(GraphNode node, DepanFxTreeSection section) {
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
}
