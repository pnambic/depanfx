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
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javafx.scene.control.TreeItem;

public abstract class DepanFxNodeListFork extends DepanFxNodeListGraphNode {

  public DepanFxNodeListFork(GraphNode node, DepanFxNodeListSection section) {
    super(node, section);
  }

  /**
   * Direct members of graph node for this tree fork.
   */
  public Collection<GraphNode> getMembers() {
    return getTreeModel().getMembers(getGraphNode());
  }

  /**
   * Transitive collection of all members below the graph node for this
   * tree fork.  The graph node for this tree fork will not included
   * (unless there is a loop in the graph .. oops).
   */
  public Collection<GraphNode> getDecendants() {

    Set<GraphNode> roots = Collections.singleton(getGraphNode());
    Collection<GraphNode> filter =
        getSection().getSectionNodes().getNodes();
    return getTreeModel()
        .getReachableGraphNodes(roots, filter)
        .getNodes();
  }

  public DepanFxTreeModel getTreeModel() {
    return ((DepanFxTreeSection) getSection()).getTreeModel();
  }

  public void sortTreeItems(
      List<TreeItem<DepanFxNodeListMember>> items) {
    getSection().sortTreeItems(items);
  }

  public TreeItem<DepanFxNodeListMember> buildTreeMember(GraphNode node) {
    return getSection().buildNodeItem(node);
  }
}
