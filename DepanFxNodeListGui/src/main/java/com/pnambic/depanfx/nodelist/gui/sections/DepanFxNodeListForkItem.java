/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListItem;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * A tree list item representing a node with children.
 */
public abstract class DepanFxNodeListForkItem extends DepanFxNodeListItem {

  // Fork/Directory actions
  public static final String SELECT_RECURSIVE = "Select Recursive";

  public static final String CLEAR_RECURSIVE = "Clear Recursive";

  public static final String EXPAND_TREE_5 = "Expand Tree (5)";

  public static final String EXPAND_TREE_20 = "Expand Tree (20)";

  public static final String EXPAND_TREE_100 = "Expand Tree (100)";

  private boolean treeLoaded = false;

  public DepanFxNodeListForkItem(DepanFxNodeListFork fork) {
    super(fork);
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

  /////////////////////////////////////
  // Utilities for derived classes.

  protected DepanFxNodeListFork getFork() {
    return (DepanFxNodeListFork) getValue();
  }

  protected void appendRecursiveActionItems(
      DepanFxContextMenuBuilder builder,
      DepanFxNodeListTableAdapter tableAdapter) {
    builder.appendActionItem(
        SELECT_RECURSIVE,
        e -> runSelectRecursiveAction(getFork(), tableAdapter, true));
    builder.appendActionItem(
        CLEAR_RECURSIVE,
        e -> runSelectRecursiveAction(getFork(), tableAdapter, false));
  }

  protected void appendExpandTreeActionItems(
      DepanFxContextMenuBuilder builder) {
    builder.appendActionItem(
        EXPAND_TREE_5, e -> runExpandTreeAction(5));
    builder.appendActionItem(
        EXPAND_TREE_20, e -> runExpandTreeAction(20));
    builder.appendActionItem(
        EXPAND_TREE_100, e -> runExpandTreeAction(100));
  }

  /////////////////////////////////////
  // Overridable.

  protected ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {
    DepanFxTreeFork folder = (DepanFxTreeFork) getValue();

    Collection<GraphNode> nodes = folder.getMembers();

    List<TreeItem<DepanFxNodeListMember>> result =
        new ArrayList<>(nodes.size());
    nodes.stream()
      .map(folder::buildTreeMember)
      .forEach(result::add);
    folder.sortTreeItems(result);

    return FXCollections.observableList(result);
  }

  private void runSelectRecursiveAction(
      DepanFxNodeListFork fork,
      DepanFxNodeListTableAdapter tableAdapter, boolean value) {
    GraphNode rootNode = fork.getGraphNode();
    tableAdapter.doSelectGraphNodeAction(rootNode, value);

    // Then all the reachable nodes.
    Collection<GraphNode> nodes = fork.getDecendants();
    tableAdapter.doSelectGraphNodesAction(nodes.stream(), value);
  }

  private void runExpandTreeAction(int expandLimit) {
    BreadthExpander expander = new BreadthExpander(expandLimit);
    expander.addBreadthItems(this.getChildren());
    expander.expandTree();
    this.setExpanded(true);
  }

  private static class BreadthExpander {

    private int expandLimit;

    private List<TreeItem<DepanFxNodeListMember>> breadthItems =
        new ArrayList<>();

    public BreadthExpander(int expandLimit) {
      this.expandLimit = expandLimit;
    }

    public void addBreadthItems(
        List<TreeItem<DepanFxNodeListMember>> moreItems) {
      breadthItems.addAll(moreItems);
    }

    public void expandTree() {
      for (int next = 0; next < breadthItems.size(); next++) {
        if (expandLimit <= 0) {
          return;
        }
        TreeItem<DepanFxNodeListMember> currItem = breadthItems.get(next);
        ObservableList<TreeItem<DepanFxNodeListMember>> children =
            currItem.getChildren();
        addBreadthItems(children);
        if (!currItem.isExpanded()) {
          expandLimit -= children.size();
          currItem.setExpanded(true);
        }
      }
    }
  }
}
