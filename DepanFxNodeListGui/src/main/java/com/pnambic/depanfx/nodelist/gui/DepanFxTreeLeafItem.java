package com.pnambic.depanfx.nodelist.gui;

public class DepanFxTreeLeafItem extends DepanFxNodeListItem {

  public DepanFxTreeLeafItem(DepanFxTreeLeaf leaf) {
    super(leaf);
  }

  @Override
  public boolean isLeaf() {
    return true;
  }
}
