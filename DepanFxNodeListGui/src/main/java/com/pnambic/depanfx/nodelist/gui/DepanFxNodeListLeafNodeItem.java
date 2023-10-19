package com.pnambic.depanfx.nodelist.gui;

import javafx.scene.control.TreeItem;

public class DepanFxNodeListLeafNodeItem extends TreeItem<DepanFxNodeListMember> {

  public DepanFxNodeListLeafNodeItem(DepanFxNodeListLeafNode leaf) {
    super(leaf);
  }

  @Override
  public boolean isLeaf() {
    return true;
  }
}
