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
 * Layouts the elements of a hierarchy as a planar tree on the
 * supplied {@link #zBase} coordinate.
 */
public class TreeLayoutRunner extends HierarchicalLayoutRunner {

  private final double xBase;

  private final double yBase;

  private final double zBase;

  private final double horizontalSpace;

  private final double verticalSpace;

  protected TreeLayoutRunner(
      DepanFxTreeModel treeModel,
      double xBase, double yBase, double zBase,
      double horizontalSpace, double verticalSpace) {
    super(treeModel);
    this.xBase = xBase;
    this.yBase = yBase;
    this.zBase = zBase;
    this.horizontalSpace = horizontalSpace;
    this.verticalSpace = verticalSpace;
  }

  @Override
  protected void assignNode(GraphNode node, int level, int offset) {
    assignPosition(node,
        xBase + (level * horizontalSpace),
        yBase + (offset * verticalSpace),
        zBase);
  }

  @Override
  protected int getRootLevel(Collection<GraphNode> roots) {
    return 0;
  }
}
