package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;

/**
 * Encapsulates the basic details of a node that is rendered within a section.
 */
public abstract class DepanFxNodeListGraphNode
    implements DepanFxNodeListMember {

  private final GraphNode node;

  private final DepanFxNodeListSection section;

  public DepanFxNodeListGraphNode(
      GraphNode node, DepanFxNodeListSection section) {
    this.node = node;
    this.section = section;
  }

  @Override
  public String getDisplayName() {
    return section.getDisplayName(node);
  }

  public String getSortKey() {
    return section.getSortKey(node);
  }

  public GraphNode getGraphNode() {
    return node;
  }

  protected DepanFxNodeListSection getSection() {
    return section;
  }
}
