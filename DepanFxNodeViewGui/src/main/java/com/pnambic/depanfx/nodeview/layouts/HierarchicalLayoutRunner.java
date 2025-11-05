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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

  // Shape information for each node.
  private static class TreeData {

    public int level;

    public boolean placed = false;

    public TreeData(int level) {
      this.level = level;
    }
  }

  // Detect cycles
  private List<GraphNode> pathNodes = new ArrayList<>();

  // Shape information for every visited node.
  private Map<GraphNode, TreeData> treeInfo = new HashMap<>();

  // Report cycles
  private List<List<GraphNode>> pathCycles = new ArrayList<>();

  // The treeModel may contain non-visible nodes.
  private Set<GraphNode> visibleNodes;

  public HierarchicalLayoutRunner(DepanFxTreeModel treeModel) {
    this.treeModel = treeModel;
  }

  @Override
  public void layoutNodes(Collection<GraphNode> layoutNodes) {
    visibleNodes = new HashSet<>(layoutNodes);
    Collection<GraphNode> roots = orderNodes(treeModel.getRoots());
    int level = setLevel(getRootLevel(roots));
    roots.stream()
        .forEach(r -> assignLevel(r, level));

    roots.stream()
        .forEach(r -> assignOffset(r));
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
   * Recursively assign the level for the given node and all of
   * its descendants.
   *
   * @param node GraphNode to position, along with its descendants.
   * @param level hierarchical level ("depth") to place node.
   */
  private void assignLevel(GraphNode root, int level) {
    TreeData treeData = new TreeData(level);
    treeInfo.put(root, treeData);

    if (isLeaf(root)) {
      return;
    }

    pathNodes.add(root);
    int nextLevel = setLevel(level + 1);
    for (GraphNode childNode : orderChildren(root)) {
      TreeData childInfo = treeInfo.get(childNode);
      if (childInfo == null) {
        assignLevel(childNode, nextLevel);
        continue;
      }
      List<GraphNode> cycles = checkCycle(childNode);
      if (cycles != null) {
        assignLevel(childNode, nextLevel);
        reportCycle(cycles, childNode);
        continue;
      }
      if (childInfo.level < nextLevel) {
        lowerChild(childNode, nextLevel);
        continue;  // For symmetry with the other cases.
      }
      // Other children are already at next level or lower.
    }
    pathNodes.removeLast();
  }

  private void lowerChild(GraphNode node, int level) {
    TreeData treeData = treeInfo.get(node);
    treeData.level = level;

    if (isLeaf(node)) {
      return;
    }

    int nextLevel = setLevel(level + 1);
    for (GraphNode childNode : orderChildren(node)) {
      TreeData childInfo = treeInfo.get(childNode);
      int wasLevel = childInfo.level;
      childInfo.level = Math.max(childInfo.level, nextLevel);
      if (childInfo.level > wasLevel) {
        lowerChild(childNode, nextLevel);
      }
    }
  }

  private void assignOffset(GraphNode node) {
    TreeData treeData = treeInfo.get(node);
    int nodeLevel = treeData.level;

    if (isLeaf(node)) {
      assignNode(node, nodeLevel, getCurrOffset(nodeLevel));
      treeInfo.get(node).placed = true;
      incrCurrOffset(nodeLevel);
      return;
    }

    int nextLevel = setLevel(nodeLevel + 1);
    int childLeft = getCurrOffset(nextLevel);

    for (GraphNode childNode : orderChildren(node)) {
      if (!treeInfo.get(childNode).placed) {
        assignOffset(childNode);
      }
    }
    int childRight = getCurrOffset(nextLevel);

    // No placed children for node, treat as leaf.
    if (childRight == childLeft) {
      assignNode(node, nodeLevel, getCurrOffset(nodeLevel));
      treeInfo.get(node).placed = true;
      incrCurrOffset(nodeLevel);
      return;
    }

    // Center this node above its children
    assignNode(node, nodeLevel, (childLeft + childRight) / 2);
    treeInfo.get(node).placed = true;
  }

  private int setLevel(int newLevel) {
    maxLevel = Math.max(newLevel, maxLevel);
    return newLevel;
  }

  private List<GraphNode> checkCycle(GraphNode childNode) {
    int childIndex = pathNodes.indexOf(childNode);
    if (childIndex < 0) {
      return null;
    }
    return pathNodes.subList(childIndex, pathNodes.size());
  }

  private void reportCycle(List<GraphNode> loopPrefix, GraphNode loopNode) {
    List<GraphNode> cycleNodes = new ArrayList<>(loopPrefix.size() + 1);
    cycleNodes.addAll(loopPrefix);
    cycleNodes.add(loopNode);
    pathCycles.add(cycleNodes);
  }

  private Collection<GraphNode> orderChildren(GraphNode root) {
    return orderNodes(treeModel.getMembers(root));
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
  private Collection<GraphNode> orderNodes(Collection<GraphNode> nodes) {
    List<GraphNode> leafs = new ArrayList<>();
    List<GraphNode> inners = new ArrayList<>();
    for (GraphNode node : nodes) {

      // Only include visible nodes.
      if (!visibleNodes.contains(node)) {
        continue;
      }

      if (isLeaf(node)) {
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

  private boolean isLeaf(GraphNode root) {
    return treeModel.getMembers(root).isEmpty();
  }
}
