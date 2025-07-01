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
    NONE, OPEN, TRIANGLE, FILLED, CHEVRON, ARTISTIC;

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

  public LineRender lineRender;

  @Override
  public void draw(GL2 gl, JoglRenderer renderer) {
    if (!isVisible) {
      return;
    }
    NodeShape sourceShape = getShape(lineSource, renderer);
    if (sourceShape == null) {
      return;
    }
    NodeShape targetShape = getShape(lineTarget, renderer);
    if (targetShape == null) {
      return;
    }

    // Drop lines that resolve to the same node folding nest.
    if (sourceShape == targetShape) {
      return;
    }

    if (lineRender == null) {
      lineRender = buildRenderer();
    }
    lineRender.prepare(this, sourceShape, targetShape);
    lineRender.draw(gl, this);
  }

  private LineRender buildRenderer() {
    if (Form.ARCED == lineForm) {
      return new RichLineRender();
    }
    return new RichLineRender();
  }

  @Override
  public void step(GL2 gl, JoglRenderer renderer) {
  }

  private NodeShape getShape(Object end, JoglRenderer renderer) {
    JoglShape sourceShape = renderer.getRenderShape(end);
    if (sourceShape instanceof NodeShape node) {
      NodeShape endNode = node.getApparentShape(renderer);
      if (endNode.isVisible) {
        return endNode;
      }
      return null;
    }
    return null;
  }

  @Override
  public LineShape forUpdate() {
    LineShape result = new LineShape();

    result.isVisible = isVisible;
    result.lineSource = lineSource;
    result.lineTarget = lineTarget;
    result.lineForm = lineForm;
    result.lineStyle = lineStyle;
    result.lineColor = lineColor;
    result.lineWidth = lineWidth;
    result.lineLabel = lineLabel;
    result.sourceArrow = sourceArrow;
    result.targetArrow = targetArrow;

    // Expect to regenerate renderer after an update.
    result.lineRender = null;
    return result;
  }
}
