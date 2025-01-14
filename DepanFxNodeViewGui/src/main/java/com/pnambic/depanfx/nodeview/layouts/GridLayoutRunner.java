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
import java.util.Collections;
import java.util.Map;

/**
 * Ensures that one layout is always available.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GridLayoutRunner extends DirectLayoutRunner {

  public static final double MIN_GRID_SPACE = 3 * DirectLayoutRunner.UNIT;

  public static final double MIN_SPACE_ROW = MIN_GRID_SPACE;

  private static final double MIN_SPACE_COL = MIN_GRID_SPACE;

  public static final double TARGET_GRID_HEIGHT = MIN_SPACE_ROW * 10;

  public static final double TARGET_GRID_WIDTH = MIN_SPACE_COL * 10;

  public enum LayoutDirection {
    HORIZONTAL {
      @Override
      public int getLimit(int rowCount, int colCount) {
          return rowCount;
      }
    },
    VERTICAL {
      @Override
      public int getLimit(int rowCount, int colCount) {
          return colCount;
      }
    };

    public abstract int getLimit(int colCount, int rowCount);
  }

  private final double xBase;

  private final double yBase;

  private final double zBase;

  private final double horizontalSpace;

  private final double verticalSpace;

  private final LayoutDirection direction;

  private int limit;

  public GridLayoutRunner(
      double xBase, double yBase, double zBase,
      double horizontalSpace, double verticalSpace,
      LayoutDirection direction, int limit) {
    this.xBase = xBase;
    this.yBase = yBase;
    this.zBase = zBase;
    this.horizontalSpace = horizontalSpace;
    this.verticalSpace = verticalSpace;
    this.direction = direction;
    this.limit = limit;
  }

  /**
   * Standardize simple grid layout.
   */
  public static Map<GraphNode, DepanFxNodeLocationData> buildNodeLocations(
      Collection<GraphNode> nodes) {
    int size = nodes.size();
    if (size == 0) {
      return Collections.emptyMap();
    }
    int width = (int) Math.ceil(Math.sqrt(size));   // aka column count, x
    int breadth = (size + width - 1) / width;       // aka row rount, y

    double spacePerColumn =
        Math.max(MIN_SPACE_COL, TARGET_GRID_WIDTH / width);
    double spacePerRow =
        Math.max(MIN_SPACE_ROW, TARGET_GRID_HEIGHT / breadth);

    // The grid runner assigns leafs from the top left
    // heading downward and to the right.
    double xBase =
        DirectLayoutRunner.Y_ORIGIN - spacePerColumn * ((width / 2.0) - 0.5);
    double yBase =
        DirectLayoutRunner.Y_ORIGIN + spacePerRow * ((breadth / 2.0) - 0.5);

    LayoutDirection direction = GridLayoutRunner.LayoutDirection.HORIZONTAL;
    GridLayoutRunner layout = new GridLayoutRunner(
        xBase, yBase, DirectLayoutRunner.Z_ORIGIN,
        spacePerColumn, spacePerRow,
        direction, direction.getLimit(width, breadth));
    layout.layoutNodes(nodes);
    return layout.getPositions(nodes);
  }

  @Override
  public void layoutNodes(Collection<GraphNode> layoutNodes) {
    populationPositions(layoutNodes);
    setDone();
  }

  private void populationPositions(Collection<GraphNode> layoutNodes) {
    // positions = new HashMap<>(layoutNodes.size());
    switch (direction) {
    case HORIZONTAL:
      populateHorizontal(layoutNodes);
      return;
    case VERTICAL:
      populateVertical(layoutNodes);
      return;
    }
  }

  private void populateHorizontal(Collection<GraphNode> layoutNodes) {

    double xCurr = xBase;
    double yCurr = yBase;

    int item = limit;
    for (GraphNode node : layoutNodes) {
      assignPosition(node, xCurr, yCurr, zBase);
      item--;
      if (item > 0) {
        xCurr += horizontalSpace;
      }
      else {
        item = limit;
        xCurr = xBase;
        yCurr -= verticalSpace;
      }
    }
  }

  private void populateVertical(Collection<GraphNode> layoutNodes) {

    double xCurr = xBase;
    double yCurr = yBase;

    int item = limit;
    for (GraphNode node : layoutNodes) {
      assignPosition(node, xCurr, yCurr, zBase);
      item--;
      if (item > 0) {
        yCurr -= verticalSpace;
      }
      else {
        item = limit;
        yCurr = yBase;
        xCurr += horizontalSpace;
      }
    }
  }
}
