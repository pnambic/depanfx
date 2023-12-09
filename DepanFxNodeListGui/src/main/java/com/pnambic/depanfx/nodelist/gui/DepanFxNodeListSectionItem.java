package com.pnambic.depanfx.nodelist.gui;

/**
 * Common behavior for all node list section items.
 */
public abstract class DepanFxNodeListSectionItem extends DepanFxNodeListItem {

  public DepanFxNodeListSectionItem(DepanFxNodeListSection section) {
    super(section);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  protected DepanFxNodeListSection getSection() {
    return (DepanFxNodeListSection) getValue();
  }
}
