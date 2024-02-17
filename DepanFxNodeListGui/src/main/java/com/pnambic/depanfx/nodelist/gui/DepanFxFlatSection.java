package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Comparator;
import java.util.List;

import javafx.scene.control.TreeItem;

public class DepanFxFlatSection implements DepanFxNodeListSection {

  public static final String EDIT_FLAT_SECTION_DATA =
      "Edit Flat Section Data...";

  public static final String NEW_FLAT_SECTION_DATA =
      "New Flat Section Data...";

  private DepanFxWorkspaceResource sectionDataRsrc;

  // Update this whenever sectionDataRsrc is revised.
  private Comparator<TreeItem<DepanFxNodeListMember>> flatMemberCompare;

  private DepanFxNodeList sectionNodes;

  public DepanFxFlatSection(
      DepanFxWorkspaceResource sectionDataRsrc) {
    this.sectionDataRsrc = sectionDataRsrc;
    this.flatMemberCompare = updateCompare();
  }

  public void setSectionDataRsrc(DepanFxWorkspaceResource sectionDataRsrc) {
    this.sectionDataRsrc = sectionDataRsrc;
    this.flatMemberCompare = updateCompare();
  }

  public DepanFxWorkspaceResource getSectionDataRsrc() {
    return sectionDataRsrc;
  }

  public DepanFxFlatSectionData getSectionData() {
    return (DepanFxFlatSectionData) sectionDataRsrc.getResource();
  }

  @Override
  public String getSectionLabel() {
    return getSectionData().getSectionLabel();
  }

  @Override
  public String getDisplayName() {
    return DepanFxNodeListSections.fmtDisplayName(
        getSectionData().getSectionLabel(),
        sectionNodes.getNodes().size(),
        getSectionData().displayNodeCount());
  }

  @Override
  public String getDisplayName(GraphNode node) {
    return node.getId().getNodeKey();
  }

  @Override
  public String getSortKey(GraphNode node) {
    OrderBy orderBy = getSectionData().getOrderBy();
    return DepanFxNodeListSections.getSortKey(node, orderBy);
  }

  @Override
  public DepanFxFlatSectionItem buildSectionItem(DepanFxNodeList baseNodes) {
    this.sectionNodes = baseNodes;
    return new DepanFxFlatSectionItem(this);
  }

  @Override
  public TreeItem<DepanFxNodeListMember> buildNodeItem(GraphNode node) {
    DepanFxNodeListLeafNode leaf = new DepanFxNodeListLeafNode(node, this);
    return new DepanFxNodeListLeafNodeItem(leaf);
  }

  @Override
  public void sortTreeItems(List<TreeItem<DepanFxNodeListMember>> items) {
    items.sort(flatMemberCompare);
  }

  @Override
  public DepanFxNodeList getSectionNodes() {
    return sectionNodes;
  }

  private Comparator<TreeItem<DepanFxNodeListMember>> updateCompare() {
    DepanFxFlatSectionData sectionData = getSectionData();
    return new FlatMemberCompare(sectionData.getOrderDirection());
  }

  private static class FlatMemberCompare
      extends DepanFxNodeListSections.CompareMembers {

    public FlatMemberCompare(OrderDirection direction) {
      super(direction);
    }

    @Override
    protected int compareMembers(
        DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo) {
      return DepanFxNodeListSections.compareBySortKey(memberOne, memberTwo);
    }
  }
}
