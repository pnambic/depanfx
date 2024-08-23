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

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.shapes.LineShape.Arrow;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Render lines with richer properties:
 * - curves provided by AWT shapes,
 * - arrowheads at ends.
 */
public class RichLineRender implements LineRender {

  /////////////////////////////////////
  // Track location of connected nodes

  private static final double ARC_THROW = 0.95;

  private double sourcePosX;

  private double sourcePosY;

  private double targetPosX;

  private double targetPosY;

  /**
   * Cache points to render as a line strip.
   */
  private LinePoints linePoints = LinePoints.EMPTY;

  private ArrowShape sourceArrow;

  private ArrowShape targetArrow;

  @Override
  public void prepare(
      LineShape line, NodeShape sourceShape, NodeShape targetShape) {

    if (haveChanged(sourceShape, targetShape)) {
      Shape lineShape = buildLineShape(
          line, sourceShape, targetShape);
      LinePointsBuilder builder = new LinePointsBuilder();
      linePoints =
          builder.prepare(lineShape, sourceShape, targetShape);

      // Attach arrowheads if there is a line
      if (linePoints.hasEndpoints()) {
        sourceArrow = buildArrow(line.sourceArrow, 1, 0);

        int targetIndex = linePoints.pointCount - 1;
        targetArrow = buildArrow(line.targetArrow, targetIndex - 1, targetIndex);
      } else {
        sourceArrow = ArrowShapes.NONE;
        targetArrow = ArrowShapes.NONE;
      }

      // And capture current position, again.
      sourcePosX = sourceShape.shapeX;
      sourcePosY = sourceShape.shapeY;
      targetPosX = targetShape.shapeX;
      targetPosY = targetShape.shapeY;
    }
  }

  @Override
  public void draw(GL2 gl, LineShape line) {

    // Skip it all if there are no vertices to draw.
    if (linePoints.hasPoints()) {
      gl.glColor3d(
          line.lineColor.red, line.lineColor.green, line.lineColor.blue);
      gl.glLineWidth((float) line.lineWidth);
      drawLinePoints(gl);
      drawHeadArrow(gl);
      drawTailArrow(gl);
    }
  }

  private boolean haveChanged(NodeShape sourceShape, NodeShape targetShape) {
    if (sourceShape.shapeX != sourcePosX) {
      return true;
    }
    if (sourceShape.shapeY != sourcePosY) {
      return true;
    }
    if (targetShape.shapeX != targetPosX) {
      return true;
    }
    if (targetShape.shapeY != targetPosY) {
      return true;
    }
    return false;
  }

  private void drawLinePoints(GL2 gl) {
    linePoints.glVertex(gl, GL2.GL_LINE_STRIP);
  }

  private void drawHeadArrow(GL2 gl) {
    gl.glPushMatrix();
    sourceArrow.draw(gl);
    gl.glPopMatrix();
  }

  private void drawTailArrow(GL2 gl) {
    gl.glPushMatrix();
    targetArrow.draw(gl);
    gl.glPopMatrix();
  }

  private ArrowShape buildArrow(
      Arrow sourceArrow, int sourceIndex, int targetIndex) {
    // Short-circuit transform computation if not used.
    if (sourceArrow == Arrow.NONE) {
      return ArrowShapes.NONE;
    }

    int sourceOffset = LinePoints.getOffset(sourceIndex);
    float sourceX = linePoints.linePoints[sourceOffset++];
    float sourceY = linePoints.linePoints[sourceOffset++];
    float sourceZ = linePoints.linePoints[sourceOffset++];

    int targetOffset = LinePoints.getOffset(targetIndex);
    float targetX = linePoints.linePoints[targetOffset++];
    float targetY = linePoints.linePoints[targetOffset++];
    float targetZ = linePoints.linePoints[targetOffset++];

    float[] transform = ArrowShapes.buildTransform(
        sourceX, sourceY, sourceZ, targetX, targetY, targetZ);

    switch (sourceArrow) {
    case ARTISTIC:
      return new ArrowShapes.Artistic(transform);
    case CHEVRON:
      return new ArrowShapes.Chevron(transform);
    case FILLED:
      return new ArrowShapes.Filled(transform);
    case OPEN:
      return new ArrowShapes.Open(transform);
    case TRIANGLE:
      return new ArrowShapes.Triangle(transform);
    default: // mostly Arrow.NONE
      break;
    }
    return ArrowShapes.NONE;
  }

  private Shape buildLineShape(
      LineShape line, NodeShape sourceShape, NodeShape targetShape) {
    switch (line.lineForm) {
    case ARCED:
      return buildArcLine(sourceShape, targetShape);
    case STRAIGHT:
      break;
    default:
      break;
    }

    return new Line2D.Double(
        sourceShape.shapeX, sourceShape.shapeY,
        targetShape.shapeX, targetShape.shapeY);
  }

  private Arc2D buildArcLine(NodeShape sourceShape, NodeShape targetShape) {

    // Calculate the midpoint
    double midX = (sourceShape.shapeX + targetShape.shapeX) / 2.0d;
    double midY = (sourceShape.shapeY + targetShape.shapeY) / 2.0d;

    // Determine the right hand perpendicular to the connecting line.
    double perpX = sourceShape.shapeY - targetShape.shapeY;
    double perpY = targetShape.shapeX - sourceShape.shapeX;

    double centerX = midX + (ARC_THROW * perpX);
    double centerY = midY + (ARC_THROW * perpY);

    double radius = Point2D.distance(
        sourceShape.shapeX, sourceShape.shapeY, centerX, centerY);

    double x = centerX - radius;
    double y = centerY - radius;
    double diam = radius * 2.0d;

    // Calculate the start angle and angular extent
    double startAngle = Math.toDegrees(
        Math.atan2(sourceShape.shapeY - centerY, sourceShape.shapeX - centerX));
    double endAngle = Math.toDegrees(
        Math.atan2(targetShape.shapeY - centerY, targetShape.shapeX - centerX));

    // Configure Arc2D.
    // Since AWT Y is reversed (increases downward) compared
    // to OGL Y (increases upward):
    // - negate start angle and extent
    // - or y = centerY + radius and -diam below.
    return new Arc2D.Double(
        x, y, diam, diam,
        -startAngle, -calcExtent(startAngle, endAngle),
        Arc2D.OPEN);
  }

  private double calcExtent(double startAngle, double endAngle) {
    double extent = endAngle - startAngle;
    if (extent > 180.d) {
      return extent - 180.0d;
    }
    if (extent < -180.d) {
      return 360 + extent;
    }
    return extent;
  }
}
