package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

public abstract class DepanFxNodeListSectionItem extends DepanFxNodeListItem {

  public DepanFxNodeListSectionItem(DepanFxNodeListMember value) {
    super(value);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  public abstract DepanFxNodeList getSectionNodes();
}
