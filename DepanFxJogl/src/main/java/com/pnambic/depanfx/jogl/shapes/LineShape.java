package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglColor;
import com.pnambic.depanfx.jogl.JoglRenderer;
import com.pnambic.depanfx.jogl.JoglShape;

public class LineShape implements JoglShape {

  public enum Form {
    STRAIGHT, ARCED;

    public static final Form DEFAULT = ARCED;
  }

  public enum Arrow {
    NONE, OPEN, TRIANGLE, FILLED, ARTISTIC;

    public static final Arrow SOURCE_DEFAULT = NONE;

    public static final Arrow TARGET_DEFAULT = ARTISTIC;
  }

  public enum Style {
    SOLID, DASHED, DOUBLE_DASH;

    public static final Style DEFAULT = SOLID;
  }

  public boolean isVisible;

  public Object lineSource;

  public Object lineTarget;

  public Form lineForm;

  public Style lineStyle;

  public JoglColor lineColor;

  public double lineWidth;

  public String lineLabel;

  public Arrow sourceArrow;

  public Arrow targetArrow;

  @Override
  public void draw(GL2 gl, JoglRenderer renderer) {
    NodeShape sourceShape = getShape(lineSource, renderer);
    if (sourceShape == null) {
      return;
    }
    NodeShape targetShape = getShape(lineTarget, renderer);
    if (targetShape == null) {
      return;
    }

    // Render the line.
    gl.glColor3d(lineColor.red, lineColor.green, lineColor.blue);
    gl.glLineWidth((float) lineWidth);

    gl.glBegin(GL2.GL_LINES);
    gl.glVertex3d(sourceShape.shapeX, sourceShape.shapeY, sourceShape.shapeZ);
    gl.glVertex3d(targetShape.shapeX, targetShape.shapeY, targetShape.shapeZ);
    gl.glEnd();
  }

  @Override
  public void step(GL2 gl, JoglRenderer renderer) {
  }

  private NodeShape getShape(Object end, JoglRenderer renderer) {
    JoglShape sourceShape = renderer.getRenderShape(end);
    if (sourceShape instanceof NodeShape) {
      return (NodeShape) sourceShape;
    }
    return null;
  }
}
