package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import java.util.List;

public class DepanFxNodeListRoot implements DepanFxNodeListMember {

  private final DepanFxWorkspace workspace;

  private final DepanFxNodeList nodeList;

  private final List<DepanFxNodeListSection> sections;

  public DepanFxNodeListRoot(
      DepanFxWorkspace workspace,
      DepanFxNodeList nodeList,
      List<DepanFxNodeListSection> sections) {
    this.workspace = workspace;
    this.nodeList = nodeList;
    this.sections = sections;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public List<DepanFxNodeListSection> getSections() {
    return sections;
  }

  public DepanFxNodeList getNodeList() {
    return nodeList;
  }

  @Override
  public String getDisplayName() {
    return "<root>";
  }
}
