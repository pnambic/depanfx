package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;

import javafx.scene.control.TreeTableCell;

public class DepanFxSimpleColumnCell
    extends TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> {

  private final DepanFxNodeListColumn nodeListColumn;

  public DepanFxSimpleColumnCell(DepanFxNodeListColumn nodeListColumn) {
    this.nodeListColumn = nodeListColumn;
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

  private void stylizeCell(DepanFxNodeListMember member) {
    if (member instanceof DepanFxNodeListGraphNode) {
      String modelKey =
          nodeListColumn.toString((DepanFxNodeListGraphNode) member);
      setText(modelKey);
      return;
    }
    setText("");
  }
}
