package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

public class DepanFxNodeListFlatSection extends DepanFxNodeListSection {

  @Override
  public String getDisplayName() {
    return "Section";
  }

  @Override
  public String getDisplayName(GraphNode node) {
    return node.getId().getNodeKey();
  }

  @Override
  public String getSortKey(GraphNode node) {
    return GraphContextKeys.toNodeKey(node.getId());
  }

  @Override
  public DepanFxNodeListFlatSectionItem buildTreeItem(DepanFxNodeList baseNodes) {
    return new DepanFxNodeListFlatSectionItem(this, baseNodes);
  }
}
