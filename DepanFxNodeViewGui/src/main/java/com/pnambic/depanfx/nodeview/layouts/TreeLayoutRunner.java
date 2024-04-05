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
 */
public class TreeLayoutRunner extends HierarchicalLayoutRunner {

  private double horizontalSpace;

  private double verticalSpace;

  protected TreeLayoutRunner(
      DepanFxTreeModel treeModel,
      double horizontalSpace,
      double verticalSpace) {
    super(treeModel);
    this.horizontalSpace = horizontalSpace;
    this.verticalSpace = verticalSpace;
  }

  @Override
  protected void assignNode(GraphNode node, int level, int offset) {
    assignPosition(node,
        level * horizontalSpace, offset * verticalSpace, 0.0d);
  }

  @Override
  protected int getRootLevel(Collection<GraphNode> roots) {
    return 0;
  }
}
