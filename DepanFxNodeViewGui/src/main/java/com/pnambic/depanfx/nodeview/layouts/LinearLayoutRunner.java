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
package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Implementation for vertical and horizontal layouts.
 */
public class LinearLayoutRunner extends DirectLayoutRunner {

  public static final double MIN_GRID_SPACE = 3 * DirectLayoutRunner.UNIT;

  public static final double MIN_SPACE_ROW = MIN_GRID_SPACE;

  private static final double MIN_SPACE_COL = MIN_GRID_SPACE;

  public static final double MIN_SPACE_STK = MIN_GRID_SPACE;

  public static final double TARGET_GRID_HEIGHT = MIN_SPACE_ROW * 10;

  public static final double TARGET_GRID_WIDTH = MIN_SPACE_COL * 10;

  public static final double TARGET_GRID_STACK = MIN_SPACE_STK * 10;

  public enum LayoutDirection {
    HORIZONTAL,
    VERTICAL,
    STACKED;
  }

  // Center the list of nodes around these coordinates.
  private final double xCenter;

  private final double yCenter;

  private final double zCenter;

  private final LayoutDirection direction;

  public LinearLayoutRunner(
      double xCenter, double yCenter, double zCenter,
      LayoutDirection direction) {
    this.xCenter = xCenter;
    this.yCenter = yCenter;
    this.zCenter = zCenter;
    this.direction = direction;
  }

  /**
   * Layout the given nodes in the given direction.
   */
  public static Map<GraphNode, DepanFxNodeLocationData> buildNodeLocations(
      Collection<GraphNode> nodes, LayoutDirection direction) {
    if (nodes.isEmpty()) {
      return Collections.emptyMap();
    }

    LinearLayoutRunner layout = new LinearLayoutRunner(
        DirectLayoutRunner.X_ORIGIN,
        DirectLayoutRunner.Y_ORIGIN,
        DirectLayoutRunner.Z_ORIGIN,
        direction);
    layout.layoutNodes(nodes);
    return layout.getPositions(nodes);
  }

  @Override
  public void layoutNodes(Collection<GraphNode> layoutNodes) {
    populationPositions(layoutNodes);
    setDone();
  }

  private void populationPositions(Collection<GraphNode> layoutNodes) {
    switch (direction) {
    case HORIZONTAL:
      populateHorizontal(layoutNodes);
      return;
    case VERTICAL:
      populateVertical(layoutNodes);
      return;
    case STACKED:
      populateStacked(layoutNodes);
      return;
    }
  }

  private void populateHorizontal(Collection<GraphNode> layoutNodes) {
    int size = layoutNodes.size();
    double spacePerColumn =
        Math.max(MIN_SPACE_COL, TARGET_GRID_WIDTH / size);
    double xBase =
        DirectLayoutRunner.X_ORIGIN - spacePerColumn * ((size / 2.0) - 0.5);

    double xCurr = xBase;

    for (GraphNode node : layoutNodes) {
      assignPosition(node, xCurr, yCenter, zCenter);
      xCurr += spacePerColumn;
    }
  }

  private void populateVertical(Collection<GraphNode> layoutNodes) {
    int size = layoutNodes.size();
    double spacePerRow =
        Math.max(MIN_SPACE_ROW, TARGET_GRID_HEIGHT / size);
    double yBase =
        DirectLayoutRunner.Y_ORIGIN + spacePerRow * ((size / 2.0) - 0.5);

    double yCurr = yBase;

    for (GraphNode node : layoutNodes) {
      assignPosition(node, xCenter, yCurr, zCenter);
      yCurr -= spacePerRow;
    }
  }

  private void populateStacked(Collection<GraphNode> layoutNodes) {
    int size = layoutNodes.size();
    double spacePerStk =
        Math.max(MIN_SPACE_STK, TARGET_GRID_STACK / size);
    double zBase =
        DirectLayoutRunner.Z_ORIGIN - spacePerStk * ((size / 2.0) - 0.5);

    double zCurr = zBase;

    for (GraphNode node : layoutNodes) {
      assignPosition(node, xCenter, yCenter, zCurr);
      zCurr += spacePerStk;
    }
  }
}
