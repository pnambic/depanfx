/*
 * Copyright 2007, 2016, 2024 The Depan Project Authors
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
package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Layout nodes based on a tree model.
 *
 * The key layout factor is the number of leafs in the visible tree model.
 */
public abstract class HierarchicalLayoutRunner extends DirectLayoutRunner {

  private final DepanFxTreeModel treeModel;

  /** Each leaf node is assigned a new position. */
  private int leafOffset = 0;

  private int maxLevel = 0;

  /** Protection from loops */
  private Set<GraphNode> allreadyDone = new HashSet<>();

  private Set<GraphNode> visibleNodes;

  public HierarchicalLayoutRunner(DepanFxTreeModel treeModel) {
    this.treeModel = treeModel;
  }

  @Override
  public void layoutNodes(Collection<GraphNode> layoutNodes) {
    visibleNodes = new HashSet<>(layoutNodes);
    Collection<GraphNode> roots = treeModel.getRoots();
    int level = setLevel(getRootLevel(roots));
    roots.stream()
        .forEach(r -> assignChildren(r, level));
    setDone();
  }

  /**
   * Provide the starting level to use for the given set of roots.
   * In radial displays, it is best to start at level 1 if there are more
   * then one root.
   *
   * @param roots collection of nodes that define the roots of the
   *     current hierarchy.
   * @return starting level to use for these roots.
   */
  protected abstract int getRootLevel(Collection<GraphNode> roots);

  /**
   * Assign the node to the provided level and offset
   * @param node GraphNode to place
   * @param level hierarchical level (depth) for the current offset
   * @param offset "horizontal" position of node in graph
   */
  protected abstract void assignNode(GraphNode node, int level, int offset);

  /**
   * Provide the current offset to use for a node at the indicated level.
   *
   * @param level hierarchical level (depth) for the current offset
   * @return offset for placement of node.
   */
  protected int getCurrOffset(int level) {
    return getLeafCount();
  }

  /**
   * Increment the current offset at the provide level, indicating that
   * the current offset has been consumed by some node placement.
   *
   * @param level hierarchical level (depth) for the current offset
   */
  protected void incrCurrOffset(int level) {
    leafOffset++;
  }

  protected int getLeafCount() {
    return leafOffset;
  }

  protected int getMaxLevel() {
    return maxLevel;
  }

  /**
   * Recursively assign the position for the given node and all of
   * it's descendants.  Through the use of an alreadyDone lookup set,
   * loops and joins in the tree data are prevented.
   *
   * @param node GraphNode to position, along with its descendants.
   * @param level hierarchical level ("depth") to place node.
   */
  private void assignChildren(GraphNode root, int level) {
    // treeModel may contain non-visible nodes.
    if (!visibleNodes.contains(root)) {
      return;
    }
    // Don't try to place an already located node.
    if (allreadyDone.contains(root)) {
      return;
    }
    allreadyDone.add(root);

    int nextLevel = setLevel(level + 1);
    int childLeft = getCurrOffset(nextLevel);
    for (GraphNode node : orderChildren(root)) {
      assignChildren(node, nextLevel);
    }

    // If there were any children, try to center this node above them
    int childRight = getCurrOffset(nextLevel);
    if (childLeft != childRight) {
      assignNode(root, level, (childLeft + childRight) / 2);
    }

    // With no children, assign to next leaf location, and bump it.
    else {
      assignNode(root, level, getCurrOffset(level));
      incrCurrOffset(level);
    }
  }

  private int setLevel(int newLevel) {
    maxLevel = Math.max(newLevel, maxLevel);
    return newLevel;
  }

  /**
   * Provide the current nodes set of children in a principled order.
   * <p>
   * The organic ordering of TreeModel.getSuccessors() has little meaning,
   * and can appear to be pretty random.  Various sorting strategies are
   * feasible: alphabetic, length of label, etc.
   * <p>
   * In this implementation, all leaf children are grouped at the front of
   * the list, with the remaining nodes at the end.  This roughly approximates
   * the view that many Filers (e.g. Package Explorer) use, with leaf files
   * and directories partitioned from each other.
   *
   * @param root node with children
   * @return Collection of children in desired processing order
   */
  private Collection<GraphNode> orderChildren(GraphNode root) {
    List<GraphNode> leafs = new ArrayList<>();
    List<GraphNode> inners = new ArrayList<>();
    for (GraphNode node : treeModel.getMembers(root)) {

      // Don't include nodes that are already placed.
      if (allreadyDone.contains(node)) {
        continue;
      }

      if (treeModel.getMembers(node).isEmpty()) {
        leafs.add(node);
      }
      else {
        inners.add(node);
      }
    }
    leafs.sort((a, b) -> orderNodes(a, b));
    inners.sort((a, b) -> orderNodes(a, b));

    leafs.addAll(inners);
    return leafs;
  }

  /**
   * Should be something better, but this is stable for now.
   */
  private int orderNodes(GraphNode nodeOne, GraphNode nodeTwo) {
    String keyOne = nodeOne.getId().getNodeKey();
    String keyTwo = nodeTwo.getId().getNodeKey();
    return keyOne.compareTo(keyTwo);
  }
}
