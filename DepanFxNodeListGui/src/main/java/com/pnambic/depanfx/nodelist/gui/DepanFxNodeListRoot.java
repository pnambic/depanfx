package com.pnambic.depanfx.nodelist.gui;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

public class DepanFxNodeListRoot extends DepanFxNodeListMember {

  private final DepanFxNodeList nodeList;

  private List<DepanFxNodeListSection> sections;

  public DepanFxNodeListRoot(
      DepanFxNodeList nodeList,
      List<DepanFxNodeListSection> sections) {
    this.nodeList = nodeList;
    this.sections = sections;
  }

  public DepanFxNodeList getNodeList() {
    return nodeList;
  }

  public List<DepanFxNodeListSection> getSections() {
    return ImmutableList.copyOf(sections);
  }

}
