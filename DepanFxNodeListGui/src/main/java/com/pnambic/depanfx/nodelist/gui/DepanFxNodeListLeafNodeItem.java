package com.pnambic.depanfx.nodelist.gui;

public class DepanFxNodeListLeafNodeItem extends DepanFxNodeListItem {

  public DepanFxNodeListLeafNodeItem(DepanFxNodeListLeafNode leaf) {
    super(leaf);
  }

  @Override
  public boolean isLeaf() {
    return true;
  }
}
