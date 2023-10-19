package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

public class DepanFxTreeSection extends DepanFxNodeListMember {

  @Override
  public String getDisplayName() {
    return "Tree";
  }

  public DepanFxNodeList pickNodes(DepanFxNodeList baseNodes) {
    // Simple for now [Oct-2023] - take them all
    // Future derived types could build trees, etc.
    return baseNodes;
  }

  public String getDisplayName(GraphNode node) {
    return node.getId().getNodeKey();
  }

  public String getSortKey(GraphNode node) {
    return GraphContextKeys.toNodeKey(node.getId());
  }
}
