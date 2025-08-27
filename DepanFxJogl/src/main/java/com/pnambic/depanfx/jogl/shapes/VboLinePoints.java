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

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import java.nio.FloatBuffer;

/**
 * Provide a sequence of line coordinates rendered via an OpenGL vertex
 * buffer object (VBO).
 */
public class VboLinePoints {

  public static final int STRIDE = 3;

  public final int pointCount;

  public final float[] linePoints;

  private int vboId;

  public static final VboLinePoints EMPTY = new VboLinePoints(0, null) {

    @Override
    public void drawPoints(GL2 gl, int mode) {
      // do nothing - avoid glBindBuffer and draw calls.
    }

    @Override
    public void dispose(GL2 gl) {
      // nothing to release
    }
  };

  public VboLinePoints(int pointCount, float[] linePoints) {
    this.pointCount = pointCount;
    this.linePoints = linePoints;
  }

  /** Build a {@link VboLinePoints} from an existing {@link LinePoints}. */
  public static VboLinePoints fromLinePoints(LinePoints src) {
    return new VboLinePoints(src.pointCount, src.linePoints);
  }

  public static int getOffset(int index) {
    return index * STRIDE;
  }

  public boolean hasEndpoints() {
    return pointCount >= 2;
  }

  public boolean hasPoints() {
    return pointCount > 0;
  }

  public void drawPoints(GL2 gl, int mode) {
    ensureVbo(gl);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboId);
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
    gl.glDrawArrays(mode, 0, pointCount);
    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }

  /** Release any OpenGL buffers created for this instance. */
  public void dispose(GL2 gl) {
    if (vboId != 0) {
      gl.glDeleteBuffers(1, new int[] { vboId }, 0);
      vboId = 0;
    }
  }

  private void ensureVbo(GL2 gl) {
    if (vboId != 0) {
      return;
    }
    int[] ids = new int[1];
    gl.glGenBuffers(1, ids, 0);
    vboId = ids[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboId);
    FloatBuffer buf = Buffers.newDirectFloatBuffer(linePoints);
    gl.glBufferData(
        GL.GL_ARRAY_BUFFER, linePoints.length * Float.BYTES, buf, GL.GL_STATIC_DRAW);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }
}
