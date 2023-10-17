package com.pnambic.depanfx.nodelist.gui;

import javafx.scene.control.TreeCell;

public class DepanFxNodeListCell extends TreeCell<DepanFxNodeListMember> {

  @Override
  protected void updateItem(DepanFxNodeListMember member, boolean empty) {
    super.updateItem(member, empty);

    // Visual space reserved for future use.
    if (empty) {
      setText(null);
      setGraphic(null);
      return;
    }
    // The normal case.
    if (member != null) {
      setText(member.getDisplayName());
      setGraphic(null);
      stylizeCell(member);
      return;
    }
    // Something unexpected.
    setText("<null>");
    setGraphic(null);
  }

  private void stylizeCell(DepanFxNodeListMember member) {
  }
}
