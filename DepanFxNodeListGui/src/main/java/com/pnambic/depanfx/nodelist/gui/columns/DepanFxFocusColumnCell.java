package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepanFxFocusColumnCell extends DepanFxSimpleColumnCell {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxFocusColumnCell.class);

  public DepanFxFocusColumnCell(DepanFxNodeListColumn nodeListColumn) {
    super(nodeListColumn);
  }

  @Override
  protected void stylizeCell(DepanFxNodeListMember member) {
    LOG.info("styling {}", member.getDisplayName());
    super.stylizeCell(member);

    if (member instanceof DepanFxNodeListGraphNode) {
      setOnMouseClicked(e -> toggleNode((DepanFxNodeListGraphNode) member));
      return;
    }
  }

  private void toggleNode(DepanFxNodeListGraphNode member) {
    GraphNode graphNode = member.getGraphNode();
    LOG.info("toggle {}", graphNode.getId().getNodeKey());
    DepanFxFocusColumn focusColumn = (DepanFxFocusColumn) getColumn();
    focusColumn.toggleNode(graphNode);

    String modelKey = focusColumn.toString((DepanFxNodeListGraphNode) member);
    setText(modelKey);
  }
}
