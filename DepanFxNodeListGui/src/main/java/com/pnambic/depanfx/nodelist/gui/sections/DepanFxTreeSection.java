package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchers;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModelBuilder;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TreeItem;

public class DepanFxTreeSection extends DepanFxNodeTreeSection {

  public static final String NEW_TREE_SECTION_DATA =
      "New Tree Section Data...";

  public static final String EDIT_TREE_SECTION_DATA =
      "Edit Tree Section Data...";

  static final Logger LOG =
      LoggerFactory.getLogger(DepanFxTreeSection.class);

  private DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionDataRsrc;

  private DepanFxTreeModel treeTreeModel;

  public DepanFxTreeSection(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionDataRsrc) {
    super(tableAdapter);
    this.sectionDataRsrc = sectionDataRsrc;
    updateCompare();
  }

  @Override // DepanFxNodeListSection
  public String getSectionLabel() {
    return getSectionData().getSectionLabel();
  }

  @Override // DepanFxNodeListSection
  public String getDisplayName() {
    return DepanFxNodeListSections.fmtDisplayName(
        getSectionData().getSectionLabel(),
        getSectionNodes().getNodes().size(),
        getSectionData().displayNodeCount());
  }

  @Override // DepanFxNodeListSection
  public String getSortKey(GraphNode node) {
    OrderBy orderBy = getSectionData().getOrderBy();
    return DepanFxNodeListSections.getSortKey(node, orderBy);
  }

  @Override // DepanFxNodeListSection
  public DepanFxNodeListSectionItem buildSectionItem(
      DepanFxNodeList baseNodes) {
    DepanFxTreeModelBuilder builder =
        new DepanFxTreeModelBuilder(getLinkMatcher());
    DepanFxTreeModel treeModel = builder.traverseGraph(
        baseNodes.getGraphDocResource(), baseNodes.getNodes());
    DepanFxNodeList sectionNodes = treeModel.getReachableGraphNodes(
        treeModel.getRoots(), baseNodes.getNodes());
    updateTreeModel(treeModel, sectionNodes);
    return new DepanFxTreeSectionItem(this);
  }

  @Override
  public TreeItem<DepanFxNodeListMember> buildNodeItem(GraphNode node) {
    switch (getTreeMode(node)) {
    case FORK:
      DepanFxTreeFork treeFork = buildMemberTreeFork(node);
      return new DepanFxTreeForkItem(treeFork);
    case LEAF:
      DepanFxTreeLeaf treeLeaf = buildMemberTreeLeaf(node);
      return new DepanFxTreeLeafItem(treeLeaf);
    }
    return null;
  }

  public DepanFxWorkspaceResource<DepanFxTreeSectionData> getSectionResource() {
    return sectionDataRsrc;
  }

  @Override
  public DepanFxTreeModel getTreeModel() {
    return treeTreeModel;
  }

  @Override
  protected void updateTreeModel(
      DepanFxTreeModel treeModel, DepanFxNodeList sectionNodes) {
    this.treeTreeModel = treeModel;
    updateSectionNodes(sectionNodes);
  }

  public void setSectionDataRsrc(
      DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionDataRsrc) {
    this.sectionDataRsrc = sectionDataRsrc;
    updateCompare();
  }

  private DepanFxTreeSectionData getSectionData() {
    return sectionDataRsrc.getResource();
  }

  private void updateCompare() {
    DepanFxTreeSectionData sectionData = getSectionData();
    updateTreeCompare(
        sectionData.getOrderDirection(), sectionData.getContainerOrder());
  }

  private DepanFxTreeFork buildMemberTreeFork(GraphNode fork) {
    return new DepanFxTreeFork(fork, this);
  }

  private DepanFxTreeLeaf buildMemberTreeLeaf(GraphNode leaf) {
    return new DepanFxTreeLeaf(leaf, this);
  }

  private DepanFxLinkMatcher getLinkMatcher() {
    DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> linkMatcherRsrc =
        getSectionData().getLinkMatcherRsrc();

    // If the section data provides a matcher, use that.
    // Otherwise, find a matcher based on the graph's context model.
    if (linkMatcherRsrc != null) {
      DepanFxLinkMatcher matcher = linkMatcherRsrc.getResource().getMatcher();
      if (matcher != null) {
        return matcher;
      }
    }

    // Use the context model from the viewer to find a good link matcher.
    ContextModelId modelId = getGraphDoc().getContextModelId();
    return DepanFxLinkMatcherGroup
        .getMemberMatcherRsrc(getWorkspace(), modelId)
        .map(r -> r.getResource().getMatcher())
        .orElseGet(this::getEmptyLinkMatcher);
  }

  private DepanFxLinkMatcher getEmptyLinkMatcher() {
    LOG.warn("Unable to find link matcher for {} context model",
        getGraphDoc().getContextModelId().getContextModelKey());
    return DepanFxLinkMatchers.EMPTY_MATCHER;
  }
}
