/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.jogl.shapes;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines and builds a compact representation of a line segment.
 */
public class LinePointsBuilder {

  private class ShapeBoundary {

    /**
     * Whether the next vertex is to the positive or negative.
     */
    private final int direction;

    /**
     * Once the boundary is calculated, this is the index of the shapeVertices
     * array that is outside the shape.
     */
    private int frontierIndex;

    /**
     * Once the boundary is calculated, this the connection point for the shape.
     */
    private float[] connectionVertex;

    /*
     * Remaining tries to find an outer vertex.
     */
    private int vertexLimit;

    public ShapeBoundary(int frontierIndex, int direction, int vertexLimit) {
      this.frontierIndex = frontierIndex;
      this.direction = direction;
      this.vertexLimit = vertexLimit;
    }

    public void calcShapeBoundary(NodeShape boundaryShape) {
      float[] anchorVertex = shapeVertices.get(frontierIndex);
      while (isInside(boundaryShape, anchorVertex[0], anchorVertex[1])) {
        frontierIndex = frontierIndex + direction;
        if (vertexLimit <= 0) {
          connectionVertex = null;
          return;
        }
        --vertexLimit;
        anchorVertex = shapeVertices.get(frontierIndex);
      }

      float outerX = anchorVertex[0];
      float outerY = anchorVertex[1];

      float[] inVertex = shapeVertices.get(frontierIndex - direction);
      float innerX = inVertex[0];
      float innerY = inVertex[1];

      float lastX = innerX;
      float lastY = innerY;

      for (;;) {
        float moveX = (outerX + innerX) / 2;
        float moveY = (outerY + innerY) / 2;

        if (atBoundary(moveX, lastX, moveY, lastY)) {
          connectionVertex = new float[] { moveX, moveY, 0.0f };
          return;
        }

        if (isInside(boundaryShape, moveX, moveY)) {
          innerX = moveX;
          innerY = moveY;
        } else {
          outerX = moveX;
          outerY = moveY;
        }

        lastX = moveX;
        lastY = moveY;
      }
    }

    public int getFrontierIndex() {
      return frontierIndex;
    }

    public float[] getConnectionVertex() {
      return connectionVertex;
    }

    private boolean isInside(
        NodeShape boundaryShape, float vertexX, float vertexY) {
      double testX = transformX(boundaryShape, vertexX);
      double testY = transformY(boundaryShape, vertexY);
      return boundaryShape.contains(testX, testY);
    }

    private double transformX(NodeShape boundaryShape, double vertexX) {
      return vertexX - boundaryShape.shapeX;
    }

    private double transformY(NodeShape boundaryShape, double vertexY) {
      return vertexY - boundaryShape.shapeY;
    }

    private boolean atBoundary(
        float moveX, float lastX, float moveY, float lastY) {
      if (Math.abs(moveX - lastX) > AwtShape.SHAPE_FLATNESS) {
        return false;
      }
      if (Math.abs(moveY - lastY) > AwtShape.SHAPE_FLATNESS) {
        return false;
      }
      return true;
    }
  }

  /**
   * Accumulates the vertices as the shape's path iterator is traversed.
   */
  private List<float[]> shapeVertices = new ArrayList<>();

  public LinePoints prepare(
      Shape lineShape, NodeShape sourceShape, NodeShape targetShape) {

      buildVertexList(lineShape);
      int vertexCount = shapeVertices.size();
      if (vertexCount < 2) {
        return LinePoints.EMPTY;
      }
      int vertexLimit = vertexCount - 2;
      ShapeBoundary sourceBoundary = new ShapeBoundary(1, 1, vertexLimit);
      sourceBoundary.calcShapeBoundary(sourceShape);

      ShapeBoundary targetBoundary =
          new ShapeBoundary(vertexCount - 2, -1, vertexLimit);
      targetBoundary.calcShapeBoundary(targetShape);
      return buildLinePoints(sourceBoundary, targetBoundary);
  }

  private LinePoints buildLinePoints(
      ShapeBoundary sourceBoundary, ShapeBoundary targetBoundary) {

    // Too much overlap of the boundary shapes.
    if (targetBoundary.getFrontierIndex() + 1 < sourceBoundary.getFrontierIndex()) {
      return LinePoints.EMPTY;
    };

    // The two endpoints (2), plus any intervening points (delta + 1).
    int pointCount = 3 +
        targetBoundary.getFrontierIndex() - sourceBoundary.getFrontierIndex();

    float[] linePoints = new float[pointCount * 3];
    int insert = 0;

    insert = installLineVertex(
        linePoints, insert, sourceBoundary.getConnectionVertex());

    for (int index = sourceBoundary.getFrontierIndex();
        index <= targetBoundary.getFrontierIndex();
        index++) {
      insert = installLineVertex(
          linePoints, insert, shapeVertices.get(index));
    }

    installLineVertex(
        linePoints, insert, targetBoundary.getConnectionVertex());
    return new LinePoints(pointCount, linePoints);
  }

  private int installLineVertex(
      float[] linePoints, int insert, float[] boundaryVertex) {
    linePoints[insert++] = boundaryVertex[0];
    linePoints[insert++] = boundaryVertex[1];
    linePoints[insert++] = boundaryVertex[2];
    return insert;
  }

  private void buildVertexList(Shape lineShape) {
    float[] currSegment = new float[6];

    PathIterator it = lineShape.getPathIterator(null, AwtShape.SHAPE_FLATNESS);
    while (!it.isDone()) {
      int res = it.currentSegment(currSegment);
      switch (res) {
        case PathIterator.SEG_CLOSE:
          break;
        case PathIterator.SEG_MOVETO:
          addShapeVertex(currSegment);
          break;
        case PathIterator.SEG_LINETO:
          addShapeVertex(currSegment);
          break;
        default:
          throw new Error("Error while drawing AWT shape. "
              + "Path iterator setment not handled:" + res);
      }
      it.next();
    }
  }

  private void addShapeVertex(float[] currSegment) {
    float[] vertex = new float[]{
        currSegment[0], currSegment[1], currSegment[2]};
    shapeVertices.add(vertex);
  }
}
