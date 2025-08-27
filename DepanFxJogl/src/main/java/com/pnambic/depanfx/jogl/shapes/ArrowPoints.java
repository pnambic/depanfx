/*
 * Copyright 2024 The Depan Project Authors
 *   Direct borrowing of earlier ArrowHead.setupControlPoints(int).
 *
 * Copyright 2008 The Depan Project Authors
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

/**
 * Define the vertices for different line-based arrow heads.
 *
 * <p>Artistic arrowheads are based on a four control-point model:</p>
 *
 * {@snippet:
 *     1
 *     .
 *    / \
 *   /.^.\
 *  2  3  4   3 = middlePoint.
 * }
 *
 * <p>This sequence points works well for closed lines and triangle fans, but
 * not for open line segments (i.e. {@code GL_LINE_STRIP}).</p>
 *
 * <p>Triangular arrowheads are based on a three point control-point model that
 * drops the 3/middlePoint from the artistic shape.  The remaining control
 * point vertices remain in the same location, but are rendered in a different
 * order.  In order to support open line strips, closed line loops,
 * and triangle fans, the vertices are sequenced in the order [ 2, 1, 4 ].</p>
 */
public class ArrowPoints {

  public enum Style {
    TRIANGLE {
      @Override
      public VboLinePoints buildPoints() {
        return buildTrianglePoints();
      }
    },
    ARTISTIC {
      @Override
      public VboLinePoints buildPoints() {
        return buildArtisticPoints();
      }
    };

    public abstract VboLinePoints buildPoints();
  }

  public static final double SEMI_CIRCLE = 180.0d;

  public static final double NOSE_ANGLE = 20.0d;

  public static final double NOTCH_DEPTH = 0.7d;

  public static final double ARROW_TIP_X = 0.0d;

  public static final double ARROW_TIP_Y = 0.0d;

  public static final VboLinePoints X_ARTISTIC_ARROW_POINTS =
      buildArtisticPoints();

  public static final VboLinePoints X_TRIANGLE_ARROW_POINTS =
      buildTrianglePoints();

  private ArrowPoints() {
    // Prevent instantiation.
  }

  /**
   * Use a different order for the points (2, 1, 4), so GL_LINE_STRIP,
   * LINE_LOOP, and GL_TRIANGLE_FAN all work.
   */
  public static VboLinePoints buildTrianglePoints() {

    double secondPointAngle = calcSecondPointAngle();
    double fourthPointAngle = calcFourthPointAngle();

    float[] arrowPoints = new float[3 * 3];
    int insert = 0;

    // Push Point 2
    insert = insertPoint(arrowPoints, insert,
        Math.cos(secondPointAngle), Math.sin(secondPointAngle));

    // Push Point 1
    insert = insertPoint(arrowPoints, insert, ARROW_TIP_X, ARROW_TIP_Y);

    // Push Point 4
    insert = insertPoint(arrowPoints, insert,
        Math.cos(fourthPointAngle), Math.sin(fourthPointAngle));
    return new VboLinePoints(3, arrowPoints);
  }

  /**
   * Provide four vertices (1, 2, 3, 4) that work for LINE_LOOP
   * and GL_TRIANGLE_FAN.
   *
   * <p>This vertex sequence is not useful with GL_LINE_STRIP</p>
   */
  public static VboLinePoints buildArtisticPoints() {

    double secondPointAngle = calcSecondPointAngle();

    double fourthPointAngle = calcFourthPointAngle();

    float[] arrowPoints = new float[4 * 3];
    int insert = 0;

    // Push Point 1
    insert = insertPoint(arrowPoints, insert, ARROW_TIP_X, ARROW_TIP_Y);

    // Push Point 2
    insert = insertPoint(arrowPoints, insert,
        Math.cos(secondPointAngle), Math.sin(secondPointAngle));

    // Push Point 3
    insert = insertPoint(arrowPoints, insert, -NOTCH_DEPTH, ARROW_TIP_Y);

    // Push Point 4
    insertPoint(arrowPoints, insert,
        Math.cos(fourthPointAngle), Math.sin(fourthPointAngle));
    return new VboLinePoints(4, arrowPoints);
  }

  private static double calcSecondPointAngle() {
    return Math.toRadians(SEMI_CIRCLE - NOSE_ANGLE);
  }

  private static double calcFourthPointAngle() {
    return Math.toRadians(SEMI_CIRCLE + NOSE_ANGLE);
  }

  private static int insertPoint(
      float[] arrowPoints, int insert, double pointX, double pointY) {
    return insertPoint(arrowPoints, insert, (float) pointX, (float) pointY);
  }

  private static int insertPoint(
      float[] arrowPoints, int insert, float pointX, float pointY) {
    arrowPoints[insert++] = pointX;
    arrowPoints[insert++] = pointY;
    arrowPoints[insert++] = 0.0f;
    return insert;
  }
}
