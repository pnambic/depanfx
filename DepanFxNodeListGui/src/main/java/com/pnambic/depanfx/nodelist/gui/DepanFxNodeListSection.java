package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

public class DepanFxNodeListSection extends DepanFxNodeListMember {

  @Override
  public String getMemberName() {
    return "Section";
  }

  public DepanFxNodeList pickNodes(DepanFxNodeList baseNodes) {
    // Simple for now [Oct-2023] - take them all
    // Future derived types could build trees, etc.
    return baseNodes;
  }
}
