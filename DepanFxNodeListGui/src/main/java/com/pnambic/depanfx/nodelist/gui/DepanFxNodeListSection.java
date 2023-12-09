package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import java.util.List;

import javafx.scene.control.TreeItem;

public interface DepanFxNodeListSection extends DepanFxNodeListMember {

  String getSectionLabel();

  /** Provide the display name for a constituent graph node. */
  String getDisplayName(GraphNode node);

  /** Provide the display name for a constituent graph node. */
  String getSortKey(GraphNode node);

  /** Build the section list member (root) for the supplied node list. */
  DepanFxNodeListSectionItem buildSectionItem(DepanFxNodeList baseNodes);

  TreeItem<DepanFxNodeListMember> buildNodeItem(GraphNode node);

  void sortTreeItems(List<TreeItem<DepanFxNodeListMember>> items);

  /**
   * Provide a list of all nodes controlled by this section.
   * These nodes will be omitted from following sections.
   */
  DepanFxNodeList getSectionNodes();
}
