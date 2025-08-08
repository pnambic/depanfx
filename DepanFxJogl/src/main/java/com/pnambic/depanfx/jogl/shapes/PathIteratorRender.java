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
package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper that caches a shape's path segments so that rendering does not
 * repeatedly traverse the {@link PathIterator} for every draw call.
 */
public class PathIteratorRender {
  /**
   * Encapsulates one segment from a {@link PathIterator}.
   */
  private static class Segment {

    final int type;

    final float[] coords;

    Segment(int type, float[] coords) {
      this.type = type;
      this.coords = coords;
    }
  }

  private final Segment[] segments;

  /**
   * Construct a cached path from the supplied {@link Shape} using the
   * {@link AwtShape#SHAPE_FLATNESS default flatness} for curves.
   */
  public PathIteratorRender(Shape shape) {
    this(shape.getPathIterator(null, NodeShape.SHAPE_FLATNESS));
  }

  /**
   * Construct a cached path from an existing {@link PathIterator}.
   */
  public PathIteratorRender(PathIterator it) {
    segments = buildSegments(it);
  }

  private static Segment[] buildSegments(PathIterator it) {
    List<Segment> result = new ArrayList<>();
    float[] coords = new float[6];
    while (!it.isDone()) {
      int type = it.currentSegment(coords);
      result.add(new Segment(type, copyCoords(type, coords)));
      it.next();
    }
    return result.toArray(new Segment[result.size()]);
  }

  /**
   * Replay the cached path segments to draw the shape fill.
   */
  public void drawShape(GL2 gl) {
    int opened = 0;
    int closed = 0;
    for (Segment s : segments) {
      switch (s.type) {
        case PathIterator.SEG_CLOSE:
          gl.glEnd();
          closed++;
          break;
        case PathIterator.SEG_MOVETO:
          gl.glBegin(GL2.GL_TRIANGLE_FAN);
          opened++;
          gl.glVertex3f(s.coords[0], s.coords[1], NodeShape.ZERO_FLOAT);
          break;
        case PathIterator.SEG_LINETO:
          gl.glVertex3f(s.coords[0], s.coords[1], NodeShape.ZERO_FLOAT);
          break;
        default:
          throw new Error("Error while drawing cached shape. "
              + "Path iterator segment not handled:" + s.type);
      }
    }
    while (closed < opened) {
      gl.glEnd();
      closed++;
    }
  }

  /**
   * Replay the cached path segments to draw the shape border.
   */
  public void drawBorder(GL2 gl) {
    int opened = 0;
    int closed = 0;
    for (Segment s : segments) {
      switch (s.type) {
        case PathIterator.SEG_CLOSE:
          gl.glEnd();
          closed++;
          break;
        case PathIterator.SEG_MOVETO:
          gl.glBegin(GL2.GL_LINE_LOOP);
          opened++;
          gl.glVertex3f(s.coords[0], s.coords[1], NodeShape.ZERO_FLOAT);
          break;
        case PathIterator.SEG_LINETO:
          gl.glVertex3f(s.coords[0], s.coords[1], NodeShape.ZERO_FLOAT);
          break;
        default:
          throw new Error("Error while drawing cached border. "
              + "Path iterator segment not handled:" + s.type);
      }
    }
    while (closed < opened) {
      gl.glEnd();
      closed++;
    }
  }

  private static float[] copyCoords(int type, float[] src) {
    return switch (type) {
      case PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO ->
          new float[] { src[0], src[1] };
      case PathIterator.SEG_QUADTO ->
          new float[] { src[0], src[1], src[2], src[3] };
      case PathIterator.SEG_CUBICTO ->
          Arrays.copyOf(src, 6);
      case PathIterator.SEG_CLOSE ->
          new float[0];
      default ->
          throw new Error("Unsupported path segment: " + type);
    };
  }
}
