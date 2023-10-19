package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import java.util.List;

public class DepanFxNodeListRoot extends DepanFxNodeListMember {

  private final DepanFxNodeList nodeList;

  private final List<DepanFxNodeListSection> sections;

  public DepanFxNodeListRoot(DepanFxNodeList nodeList,
      List<DepanFxNodeListSection> sections) {
    this.nodeList = nodeList;
    this.sections = sections;
  }

  public List<DepanFxNodeListSection> getSections() {
    return sections;
  }

  public DepanFxNodeList getNodeList() {
    return nodeList;
  }
}
