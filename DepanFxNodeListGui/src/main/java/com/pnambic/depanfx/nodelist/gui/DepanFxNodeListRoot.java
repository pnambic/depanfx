package com.pnambic.depanfx.nodelist.gui;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

public class DepanFxNodeListRoot extends DepanFxNodeListMember {

  private final DepanFxNodeList nodes;

  private List<DepanFxNodeListSection> sections;

  public DepanFxNodeListRoot(
      DepanFxNodeList nodes,
      List<DepanFxNodeListSection> sections) {
    this.nodes = nodes;
    this.sections = sections;
  }

  public List<DepanFxNodeListSection> getSections() {
    return ImmutableList.copyOf(sections);
  }

}
