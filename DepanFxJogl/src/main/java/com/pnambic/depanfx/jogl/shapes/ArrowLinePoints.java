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

public class ArrowLinePoints {

  public static final double ANGLE = 20.0;

  public static final double DEPTH = 0.7;

  public static final LinePoints ARTISTIC_ARROW_POINTS = buildArtisticPoints();

  public static final LinePoints TRIANGLE_ARROW_POINTS = buildTrianglePoints();

  private  ArrowLinePoints() {
    // Prevent instantiation.
  }

  /**
   * Use a different order for the points (2, 1, 4), so GL_LINE_STRIP,
   * LINE_LOOP, and GL_TRIANGLE_FAN all work.
   * @return
   */
  public static LinePoints buildTrianglePoints() {
    // TODO(tugrul): Convert this to use a list of x,y pairs that are rotated
    // into position as needed.
    //
    // initialize from one list or the other.
    // arrow3Point = {{1, 0}, {0, 4}, {-1, 0}};
    // arrow4Point = {{1, 0}, {0, 4}, {-1, 0}, {0, 1}};

    float x = 0f;
    float y = 0f;
    float size = 1f;
    double rotation = 0;

    //   1
    //   .
    //  / \
    // /.^.\
    //2  3  4   3 = middlePoint.
    final float secondPointAngle = (float) ((270.0 - ANGLE) * 2.0 * Math.PI
        / 360.0 + rotation); // in rad.
    final float fourthPointAngle = (float) ((270.0 + ANGLE) * 2.0 * Math.PI
        / 360.0 + rotation); // in rad.

    float[] arrowPoints = new float[3 * 3];
    int insert = 0;

    // Push Point 2
    insert = insertPoint(arrowPoints, insert,
        x + Math.cos(secondPointAngle) * size,
        y + Math.sin(secondPointAngle) * size);

    // Push Point 1
    insert = insertPoint(arrowPoints, insert, x, y);

    // Push Point 4
    insert = insertPoint(arrowPoints, insert,
        x + Math.cos(fourthPointAngle) * size,
        y + Math.sin(fourthPointAngle) * size);
    return new LinePoints(3, arrowPoints);
  }

  public static LinePoints buildArtisticPoints() {
    // TODO(tugrul): Convert this to use a list of x,y pairs that are rotated
    // into position as needed.
    //
    // initialize from one list or the other.
    // arrow3Point = {{1, 0}, {0, 4}, {-1, 0}};
    // arrow4Point = {{1, 0}, {0, 4}, {-1, 0}, {0, 1}};

    float x = 0f;
    float y = 0f;
    float size = 1f;
    double rotation = 0;

    //   1
    //   .
    //  / \
    // /.^.\
    //2  3  4   3 = middlePoint.
    final float secondPointAngle = (float) ((270.0 - ANGLE) * 2.0 * Math.PI
        / 360.0 + rotation); // in rad.
    final float fourthPointAngle = (float) ((270.0 + ANGLE) * 2.0 * Math.PI
        / 360.0 + rotation); // in rad.

    float[] arrowPoints = new float[4 * 3];
    int insert = 0;

    // Push Point 1
    insertPoint(arrowPoints, insert, x, y);

    // Push Point 2
    insertPoint(arrowPoints, insert,
        x + Math.cos(secondPointAngle) * size,
        y + Math.sin(secondPointAngle) * size);

      final float middlePointAngle =
          (float) (270.0 * 2.0 * Math.PI / 360.0 + rotation); // in rad.
      insertPoint(arrowPoints, insert,
          x + Math.cos(middlePointAngle) * DEPTH * size,
          y + Math.sin(middlePointAngle) * DEPTH * size);

    // Push Point 4
    insertPoint(arrowPoints, insert,
        x + Math.cos(fourthPointAngle) * size,
        y + Math.sin(fourthPointAngle) * size);
    return new LinePoints(4, arrowPoints);
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
