package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxTreeFork;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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
      setOnMouseClicked(e -> toggleNode(e, (DepanFxNodeListGraphNode) member));
      // Fall through, there may be other styling.
    }
    if (member instanceof DepanFxTreeFork) {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem("Set recursive", e -> setRecursiveAction());
      builder.appendActionItem("Clear recursive", e -> clearRecursiveAction());
      builder.appendSeparator();
      builder.appendActionItem("Hoist", e -> hoistSelectsAction());
      setContextMenu(builder.build());
      return;
    }
    setContextMenu(null);
  }

  private void toggleNode(MouseEvent mouse, DepanFxNodeListGraphNode member) {
    GraphNode graphNode = member.getGraphNode();
    LOG.info("toggle {}", graphNode.getId().getNodeKey());

    if (mouse.getButton() == MouseButton.PRIMARY) {
      DepanFxFocusColumn focusColumn = getFocusColumn();
      focusColumn.toggleNode(graphNode);
      String modelKey = focusColumn.toString((DepanFxNodeListGraphNode) member);
      setText(modelKey);
    }
  }

  /////////////////////////////////////
  // Fork nodes

  private void setRecursiveAction() {
    getFocusColumn().setDecendantsCategories(getForkItem());
  }

  private void clearRecursiveAction() {
    getFocusColumn().clearDecendantsCategories(getForkItem());
  }

  private void hoistSelectsAction() {
    getFocusColumn().hoistMemberships(getForkItem());
  }

  /////////////////////////////////////
  // Type coercion

  private DepanFxFocusColumn getFocusColumn() {
    return (DepanFxFocusColumn) getColumn();
  }

  private DepanFxTreeFork getForkItem() {
    return (DepanFxTreeFork) getItem();
  }
}
