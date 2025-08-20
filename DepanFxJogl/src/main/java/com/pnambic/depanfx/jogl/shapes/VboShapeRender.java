package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import java.awt.geom.PathIterator;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper that builds and renders vertex buffer objects for a shape path.
 *
 * <p>The geometry for both the filled shape and its border are computed from a
 * {@link PathIterator} and uploaded to OpenGL VBOs on demand.  The VBOs are
 * lazily created the first time the shape or border is drawn and released when
 * {@link #dispose(GL2)} is called.</p>
 */
public class VboShapeRender {

  private final float[] fillVertices;

  private final float[] borderVertices;

  private int fillVboId;

  private int borderVboId;

  private VboShapeRender(float[] fillVertices, float[] borderVertices) {
    this.fillVertices = fillVertices;
    this.borderVertices = borderVertices;
  }

  /**
   * Build a {@link VboShapeRender} from the supplied {@link PathIterator}.
   * The iterator should only produce {@code SEG_MOVETO}, {@code SEG_LINETO}
   * and {@code SEG_CLOSE} segments.
   */
  public static VboShapeRender build(PathIterator it) {
    List<Float> verts = new ArrayList<>();
    float[] coords = new float[6];
    while (!it.isDone()) {
      int type = it.currentSegment(coords);
      switch (type) {
        case PathIterator.SEG_MOVETO:
        case PathIterator.SEG_LINETO:
          verts.add(coords[0]);
          verts.add(coords[1]);
          break;
        case PathIterator.SEG_CLOSE:
          // Nothing to record; arrays are implicitly closed by draw modes.
          break;
        default:
          throw new Error("Unsupported path iterator segment: " + type);
      }
      it.next();
    }
    float[] data = new float[verts.size()];
    for (int i = 0; i < verts.size(); i++) {
      data[i] = verts.get(i);
    }
    // Use the same vertices for shape fill and border rendering.
    return new VboShapeRender(data, data.clone());
  }

  /** Draw the filled shape using a VBO. */
  public void drawShape(GL2 gl) {
    ensureFillVbo(gl);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, fillVboId);
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glVertexPointer(2, GL.GL_FLOAT, 0, 0);
    gl.glDrawArrays(GL2.GL_TRIANGLE_FAN, 0, fillVertices.length / 2);
    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }

  /** Draw the shape border using a VBO. */
  public void drawBorder(GL2 gl) {
    ensureBorderVbo(gl);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, borderVboId);
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glVertexPointer(2, GL.GL_FLOAT, 0, 0);
    gl.glDrawArrays(GL2.GL_LINE_LOOP, 0, borderVertices.length / 2);
    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
  }

  /** Release any OpenGL buffers created for this render helper. */
  public void dispose(GL2 gl) {
    if (fillVboId != 0) {
      gl.glDeleteBuffers(1, new int[] { fillVboId }, 0);
      fillVboId = 0;
    }
    if (borderVboId != 0) {
      gl.glDeleteBuffers(1, new int[] { borderVboId }, 0);
      borderVboId = 0;
    }
  }

  private void ensureFillVbo(GL2 gl) {
    if (fillVboId != 0) {
      return;
    }
    fillVboId = allocateVbo(gl, fillVertices);
  }

  private void ensureBorderVbo(GL2 gl) {
    if (borderVboId != 0) {
      return;
    }
    borderVboId = allocateVbo(gl, borderVertices);
  }

  private int allocateVbo(GL gl, float[] vertices) {
    int[] ids = new int[1];
    gl.glGenBuffers(1, ids, 0);
    int result = ids[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, result);
    FloatBuffer buf = Buffers.newDirectFloatBuffer(vertices);
    gl.glBufferData(
        GL.GL_ARRAY_BUFFER, vertices.length * Float.BYTES, buf, GL.GL_STATIC_DRAW);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    return result;
  }
}
