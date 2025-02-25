package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListItem;

public class DepanFxFlatLeafItem extends DepanFxNodeListItem {

  public DepanFxFlatLeafItem(DepanFxFlatLeaf leaf) {
    super(leaf);
  }

  @Override
  public boolean isLeaf() {
    return true;
  }
}
