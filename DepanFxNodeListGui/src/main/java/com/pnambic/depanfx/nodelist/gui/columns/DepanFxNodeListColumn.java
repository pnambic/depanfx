package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;

public interface DepanFxNodeListColumn {

  String getColumnLabel();

  String toString(DepanFxNodeListGraphNode member);

  TreeTableColumn<DepanFxNodeListMember, ?> createColumn();

  void prepareCell(TreeTableCell<DepanFxNodeListMember, ?> cell);
}
