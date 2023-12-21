package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepanFxCategoryColumnCell extends DepanFxSimpleColumnCell {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxCategoryColumnCell.class);

  public DepanFxCategoryColumnCell(DepanFxNodeListColumn nodeListColumn) {
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
    DepanFxCategoryColumn categoryColumn = (DepanFxCategoryColumn) getColumn();
    // categoryColumn.toggleNode(graphNode);

    String modelKey = categoryColumn.toString((DepanFxNodeListGraphNode) member);
    setText(modelKey);
  }
}
