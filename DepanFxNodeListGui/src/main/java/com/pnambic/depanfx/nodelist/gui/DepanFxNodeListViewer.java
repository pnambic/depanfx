package com.pnambic.depanfx.nodelist.gui;

import java.util.List;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class DepanFxNodeListViewer {

  private DepanFxNodeList nodeList;

  private TreeView<DepanFxNodeListMember> nodeListView;

  private List<DepanFxNodeListSection> sections;

  public DepanFxNodeListViewer(
      DepanFxNodeList nodeList,
      List<DepanFxNodeListSection> sections) {
    this.nodeList = nodeList;
    this.sections = sections;
    this.nodeListView = createView();
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, nodeListView);
    return result;
  }

  private TreeView<DepanFxNodeListMember> createView() {
    DepanFxNodeListRoot rootMember = new DepanFxNodeListRoot(nodeList, sections);
    TreeItem<DepanFxNodeListMember> treeRoot = new DepanFxNodeListRootItem(rootMember);

    TreeView<DepanFxNodeListMember> result = new TreeView<>(treeRoot);
    result.setShowRoot(false);
    result.setCellFactory(new NodeListCellFactory());
    return result;
  }

  private class NodeListCellFactory
      implements Callback<TreeView<DepanFxNodeListMember>, TreeCell<DepanFxNodeListMember>> {

    @Override
    public TreeCell<DepanFxNodeListMember> call(TreeView<DepanFxNodeListMember> param) {

      return new DepanFxNodeListCell();
    }
  }
}
