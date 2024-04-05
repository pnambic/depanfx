/*
 * Copyright 2016, 2024 The Depan Project Authors
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
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation basics for layout runners with zero steps.
 * These lay out the nodes directly, in a single call to {@code layoutNodes()}.
 */
public abstract class DirectLayoutRunner implements LayoutRunner {

  public static final double UNIT = 5.0; // 30.0d;

  public static final double Z_ORIGIN = 0.0d;

  private Map<GraphNode, DepanFxNodeLocationData> positions = new HashMap<>();

  private boolean done = false;

  @Override
  public int layoutCost() {
    if (done) {
      return 0;
    }
    return 1;
  }

  @Override
  public void layoutStep() {
    // Nothing to do.
  }

  @Override
  public boolean layoutDone() {
    return done ;
  }

  @Override
  public Map<GraphNode, DepanFxNodeLocationData> getPositions(
      Collection<GraphNode> nodes) {
    Map<GraphNode, DepanFxNodeLocationData> result =
        new HashMap<>(nodes.size());
    nodes.stream()
        .forEach(n -> populatePositions(result, n));
    return result;
  }

  /////////////////////////////////////
  // Mutators for derived classes.

  protected void setDone() {
    done = true;
  }

  protected void assignPosition(GraphNode node,
      double xPos, double yPos, double sPos) {
    DepanFxNodeLocationData location =
        new DepanFxNodeLocationData(xPos, yPos, sPos);
    positions.put(node, location);
  }

  /////////////////////////////////////
  // Internal methods.

  private void populatePositions(
      Map<GraphNode, DepanFxNodeLocationData> populate, GraphNode node) {
    DepanFxNodeLocationData result = positions.get(node);
    if (result != null) {
      populate.put(node, result);
    }
    // No known position, don't offer one.
  }
}
