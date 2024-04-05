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

import java.util.Collection;

/**
 * Ensures that one layout is always available.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class GridLayoutRunner extends DirectLayoutRunner {

  public enum LayoutDirection {
    HORIZONTAL, VERTICAL;
  }

  private final int columns;

  private final int rows;

  private final LayoutDirection direction;

  private double horizontalSpace = UNIT;

  private double verticalSpace = UNIT;

  public GridLayoutRunner(int columns, int rows, LayoutDirection direction) {
    this.columns = columns;
    this.rows = rows;
    this.direction = direction;
  }

  @Override
  public void layoutNodes(Collection<GraphNode> layoutNodes) {
    double leftPos = - horizontalSpace * ((columns / 2.0) - 0.5);
    double topPos = verticalSpace * ((rows / 2.0) - 0.5);
    populationPositions(layoutNodes, topPos, leftPos);
    setDone();
  }

  private void populationPositions(
      Collection<GraphNode> layoutNodes, double topPos, double leftPos) {
    // positions = new HashMap<>(layoutNodes.size());
    switch (direction) {
    case HORIZONTAL:
      populateHorizontal(layoutNodes, topPos, leftPos);
      return;
    case VERTICAL:
      populateVertical(layoutNodes, topPos, leftPos);
      return;
    }
  }

  private void populateHorizontal(
      Collection<GraphNode> layoutNodes, double topPos, double leftPos) {

    double xCurr = leftPos;
    double yCurr = topPos;

    int item = (int) columns;
    for (GraphNode node : layoutNodes) {
      assignPosition(node, xCurr, yCurr, Z_ORIGIN);
      item--;
      if (item > 0) {
        xCurr += horizontalSpace;
      }
      else {
        item = (int) columns;
        xCurr = leftPos;
        yCurr -= verticalSpace;
      }
    }
  }

  private void populateVertical(
      Collection<GraphNode> layoutNodes, double topPos, double leftPos) {

    double xCurr = leftPos;
    double yCurr = topPos;

    int item = (int) rows;
    for (GraphNode node : layoutNodes) {
      assignPosition(node, xCurr, yCurr, Z_ORIGIN);
      item--;
      if (item > 0) {
        yCurr -= verticalSpace;
      }
      else {
        item = (int) rows;
        yCurr = topPos;
        xCurr += horizontalSpace;
      }
    }
  }
}
