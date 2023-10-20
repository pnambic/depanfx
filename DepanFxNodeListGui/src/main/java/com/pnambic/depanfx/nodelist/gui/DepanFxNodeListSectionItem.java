package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import javafx.scene.control.TreeItem;

public abstract class DepanFxNodeListSectionItem extends TreeItem<DepanFxNodeListMember> {

  public DepanFxNodeListSectionItem(DepanFxNodeListMember value) {
    super(value);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  public abstract DepanFxNodeList getSectionNodes();
}
