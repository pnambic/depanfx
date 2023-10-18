package com.pnambic.depanfx.nodelist.gui;

import javafx.scene.control.TreeItem;

public class DepanFxNodeListLeafNodeItem extends TreeItem<DepanFxNodeListMember> {

  public DepanFxNodeListLeafNodeItem(DepanFxNodeListLeafNode node) {
    super(node);
  }

  @Override
  public boolean isLeaf() {
    return true;
  }
}