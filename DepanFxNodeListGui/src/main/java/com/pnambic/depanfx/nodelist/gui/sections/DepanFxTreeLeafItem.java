package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListItem;

public class DepanFxTreeLeafItem extends DepanFxNodeListItem {

  public DepanFxTreeLeafItem(DepanFxTreeLeaf leaf) {
    super(leaf);
  }

  @Override
  public boolean isLeaf() {
    return true;
  }
}
