package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;

public class StraightLineRender implements LineRender {

  private NodeShape sourceShape;

  private NodeShape targetShape;

  @Override
  public void prepare(
      LineShape line, NodeShape sourceShape, NodeShape targetShape) {
    this.sourceShape = sourceShape;
    this.targetShape = targetShape;
  }

  @Override
  public void draw(LineShape line, GL2 gl) {

    // Render the line.
    gl.glColor3d(line.lineColor.red, line.lineColor.green, line.lineColor.blue);
    gl.glLineWidth((float) line.lineWidth);

    gl.glBegin(GL2.GL_LINES);
    gl.glVertex3d(sourceShape.shapeX, sourceShape.shapeY, sourceShape.shapeZ);
    gl.glVertex3d(targetShape.shapeX, targetShape.shapeY, targetShape.shapeZ);
    gl.glEnd();
  }
}
