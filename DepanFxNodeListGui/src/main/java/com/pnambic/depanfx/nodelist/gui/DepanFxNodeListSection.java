package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

public abstract class DepanFxNodeListSection extends DepanFxNodeListMember {

  public abstract String getDisplayName(GraphNode node);

  public abstract String getSortKey(GraphNode node);

  public abstract DepanFxNodeListSectionItem buildTreeItem(
      DepanFxNodeList baseNodes);

}
