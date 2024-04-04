package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * Provides top-level rendering of a tree section.
 * Used as the mechanism to provide {@link DepanFxTreeFork} elements with
 * the section's {@link DepanFxTreeModel}.
 */
public class DepanFxTreeSectionItem
    extends DepanFxNodeListSectionItem {

  private boolean treeLoaded = false;

  public DepanFxTreeSectionItem(DepanFxTreeSection section) {
    super(section);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
    if (!treeLoaded) {
      treeLoaded = true;
      super.getChildren().setAll(buildChildren());
    }

    return super.getChildren();
  }

  private ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {
    DepanFxNodeListSection section = getSection();

    DepanFxTreeModel treeModel = ((DepanFxTreeSection) section).getTreeModel();
    Collection<GraphNode> nodes = treeModel.getRoots();

    List<TreeItem<DepanFxNodeListMember>> result =
        new ArrayList<>(nodes.size());
    nodes.stream()
        .map(section::buildNodeItem)
        .forEach(result::add);
    section.sortTreeItems(result);

    return FXCollections.observableList(result);
  }
}
