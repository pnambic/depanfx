package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;

import javafx.scene.control.TreeTableCell;

public class DepanFxBaseColumnCell
    extends TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> {

  private final DepanFxNodeListColumn nodeListColumn;

  public DepanFxBaseColumnCell(DepanFxNodeListColumn nodeListColumn) {
    this.nodeListColumn = nodeListColumn;
  }

  public DepanFxNodeListColumn getColumn() {
    return nodeListColumn;
  }

  @Override
  protected void updateItem(DepanFxNodeListMember member, boolean empty) {
    // TODO Auto-generated method stub
    super.updateItem(member, empty);

    // Visual space reserved for future use.
    if (empty) {
      setText("");
      setGraphic(null);
      return;
    }
    // The normal case.
    if (member != null) {
      stylizeCell(member);
      return;
    }
    // Something unexpected.
    setText("<null>");
    setGraphic(null);
  }

  protected void stylizeCell(DepanFxNodeListMember member) {
    if (member instanceof DepanFxNodeListGraphNode node) {
      setText(nodeListColumn.toString(node));
      return;
    }
    setText("");
  }

  protected void restoreState() {
    // Hook for embolden/restoreFont and similar.
  }
}
