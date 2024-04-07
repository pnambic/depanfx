/*
 * Copyright 2017, 2024 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;

import java.util.Collection;

/**
 * Assign the nodes radial positions based on the supplied hierarchy.
 */
public class RadialLayoutRunner extends HierarchicalLayoutRunner {

  private final double xBase;

  private final double yBase;

  private final double zBase;

  private final int rootLevel;

  private final double radiansPerLeaf;

  private final double radiusPerLevel;

  public RadialLayoutRunner(
      DepanFxTreeModel treeModel,
      double xBase, double yBase, double zBase,
      int rootLevel,
      double radiansPerLeaf,
      double radiusPerLevel) {
    super(treeModel);
    this.xBase = xBase;
    this.yBase = yBase;
    this.zBase = zBase;
    this.rootLevel = rootLevel;
    this.radiansPerLeaf = radiansPerLeaf;
    this.radiusPerLevel = radiusPerLevel;
  }

  /**
   * {@inheritDoc}
   * <p>
   * For radial layouts, the center of the circle should not be occupied
   * unless their is only one root.
   */
  public static int calcRootLevel(Collection<GraphNode> roots) {

    // Don't occupy the center unless there is only one root
    int rootCount = roots.size();
    if (rootCount <= 1) {
      return 0;
    }
    if (rootCount <= 3) {
      return 1;
    }
    if (rootCount <= 9) {
      return 2;
    }
    return 3;
  }


  @Override
  protected void assignNode(GraphNode node, int level, int offset) {
    double radians = radiansPerLeaf * offset;
    double xPos = xBase + Math.cos(radians) * radiusPerLevel * level;
    double yPos = yBase + Math.sin(radians) * radiusPerLevel * level;
    assignPosition(node, xPos, yPos, zBase);
  }

  /**
   * {@inheritDoc}
   * <p>
   * For radial layouts, the center of the circle should not be occupied
   * unless their is only one root.
   */
  @Override
  protected int getRootLevel(Collection<GraphNode> roots) {
    return rootLevel;
  }
}
