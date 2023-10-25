package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel.TreeMode;

import java.util.Collection;

public class DepanFxTreeFork extends DepanFxNodeListMember
    implements DepanFxGraphNodeProvider {

  private final GraphNode node;

  private final DepanFxTreeSection section;

  private final DepanFxTreeModel treeModel;

  public DepanFxTreeFork(GraphNode node, DepanFxTreeSection section, DepanFxTreeModel treeModel) {
    this.node = node;
    this.section = section;
    this.treeModel = treeModel;
  }

  @Override
  public String getDisplayName() {
    return section.getDisplayName(node);
  }

  @Override
  public String getSortKey() {
    return section.getSortKey(node);
  }

  @Override // DepanFxGraphNodeProvider
  public GraphNode getGraphNode() {
    return node;
  }

  public Collection<GraphNode> getMembers() {
    return treeModel.getMembers(node);
  }

  public DepanFxTreeModel getTreeModel() {
    return treeModel;
  }

  public TreeMode getMemberTreeMode(GraphNode member) {
    return treeModel.getTreeMode(member);
  }

  public DepanFxTreeFork buildMemberTreeFork(GraphNode fork) {
    return new DepanFxTreeFork(fork, section, treeModel);
  }

  public DepanFxTreeLeaf buildMemberTreeLeaf(GraphNode leaf) {
    return new DepanFxTreeLeaf(leaf, section);
  }
}
