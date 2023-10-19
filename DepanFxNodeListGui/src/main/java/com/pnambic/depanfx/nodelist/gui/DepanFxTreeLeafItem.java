package com.pnambic.depanfx.nodelist.gui;

import javafx.scene.control.TreeItem;

public class DepanFxTreeLeafItem extends TreeItem<DepanFxNodeListMember> {

  public DepanFxTreeLeafItem(DepanFxTreeLeaf leaf) {
    super(leaf);
  }

  @Override
  public boolean isLeaf() {
    return true;
  }
}
