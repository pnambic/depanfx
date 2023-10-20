package com.pnambic.depanfx.nodelist.gui;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherRegistry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class DepanFxNodeListViewer {

  private final DepanFxLinkMatcherRegistry linkMatcherRegistry;

  private DepanFxNodeList nodeList;

  private List<DepanFxNodeListSection> sections;

  private TreeView<DepanFxNodeListMember> nodeListView;

  public DepanFxNodeListViewer(
      DepanFxLinkMatcherRegistry linkMatcherRegistry,
      DepanFxNodeList nodeList,
      List<DepanFxNodeListSection> sections) {
    this.linkMatcherRegistry = linkMatcherRegistry;
    this.nodeList = nodeList;
    this.sections = sections;
    this.nodeListView = createView();
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, nodeListView);
    return result;
  }

  public void insertSection(
      DepanFxNodeListSection before, DepanFxNodeListSection insert) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    int index = Integer.max(0, sections.indexOf(before));
    sections.add(index, insert);

    TreeItem<DepanFxNodeListMember> treeRoot = createTreeRoot();
    nodeListView.setRoot(treeRoot);
  }

  private List<DepanFxNodeListSection> isolateSections() {
    return ImmutableList.copyOf(sections);
  }

  private TreeItem<DepanFxNodeListMember> createTreeRoot() {
    DepanFxNodeListRoot rootMember =
        new DepanFxNodeListRoot(nodeList, isolateSections());
    return new DepanFxNodeListRootItem(rootMember);
  }

  private TreeView<DepanFxNodeListMember> createView() {
    TreeView<DepanFxNodeListMember> result = new TreeView<>(createTreeRoot());
    result.setShowRoot(false);
    result.setCellFactory(new NodeListCellFactory());
    return result;
  }

  private class NodeListCellFactory
      implements Callback<TreeView<DepanFxNodeListMember>, TreeCell<DepanFxNodeListMember>> {

    @Override
    public TreeCell<DepanFxNodeListMember> call(TreeView<DepanFxNodeListMember> param) {

      return new DepanFxNodeListCell(DepanFxNodeListViewer.this);
    }
  }

  public Optional<DepanFxLinkMatcher> getMemberLinkMatcher() {
    ContextModelId modelId =
        ((GraphDocument) nodeList.getWorkspaceResource().getResource())
        .getContextModelId();
    return linkMatcherRegistry.getMatcherByGroup(
        modelId, DepanFxLinkMatcherGroup.MEMBER);
  }
}
