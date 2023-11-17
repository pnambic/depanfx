package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

public interface DepanFxNodeListSection extends DepanFxNodeListMember {

  String getSectionLabel();

  /** Provide the display name for a constituent graph node. */
  String getDisplayName(GraphNode node);

  /** Provide the display name for a constituent graph node. */
  String getSortKey(GraphNode node);

  /** Build the section list member (root) for the supplied node list. */
  DepanFxNodeListSectionItem buildTreeItem(DepanFxNodeList baseNodes);
}
