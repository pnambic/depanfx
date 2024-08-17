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
  private LinePointsBuilder.LinePoints linePoints;

  @Override
  public void prepare(
      LineShape line, NodeShape sourceShape, NodeShape targetShape) {

    if (haveChanged(sourceShape, targetShape)) {
      Shape lineShape = buildLineShape(
          line, sourceShape, targetShape);
      LinePointsBuilder builder = new LinePointsBuilder();
      linePoints =
          builder.prepare(lineShape, sourceShape, targetShape);

      // And capture current position, again.
      sourcePosX = sourceShape.shapeX;
      sourcePosY = sourceShape.shapeY;
      targetPosX = targetShape.shapeX;
      targetPosY = targetShape.shapeY;
    }
  }

  @Override
  public void draw(LineShape line, GL2 gl) {
    gl.glColor3d(line.lineColor.red, line.lineColor.green, line.lineColor.blue);
    gl.glLineWidth((float) line.lineWidth);

    gl.glBegin(GL2.GL_LINE_STRIP);
    float[] vertices = linePoints.linePoints;

    int index = 0;  // Stride by 3
    for (int count = 0; count < linePoints.pointCount; ++count) {
      gl.glVertex3fv(vertices, index);
      index += 3;
    }
    gl.glEnd();
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
    double extent = endAngle - startAngle;

    // Configure Arc2D.
    // Since AWT Y is reversed (increases downward) compared
    // to OGL Y (increases upward):
    // - negate start angle and extent
    // - or y = centerY + radius and -diam below.
    return new Arc2D.Double(
        x, y, diam, diam, -startAngle, -extent, Arc2D.OPEN);
  }
}
