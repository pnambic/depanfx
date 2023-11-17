package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import java.text.MessageFormat;

public class DepanFxNodeListFlatSection implements DepanFxNodeListSection {

  private DepanFxNodeList baseNodes;

  @Override
  public String getSectionLabel() {
    return "Section";
  }

  @Override
  public String getDisplayName() {
    return fmtDisplayName();
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
  public DepanFxNodeListFlatSectionItem buildTreeItem(
      DepanFxNodeList baseNodes) {
    this.baseNodes = baseNodes;
    return new DepanFxNodeListFlatSectionItem(this, baseNodes);
  }

  private String fmtDisplayName() {
    int listSize = baseNodes.getNodes().size();
    if (listSize == 1) {
      return MessageFormat.format(
          "{0} ({1} node)", getSectionLabel(), listSize);

    }
    return MessageFormat.format(
        "{0} ({1} nodes)", getSectionLabel(), listSize);
  }
}
