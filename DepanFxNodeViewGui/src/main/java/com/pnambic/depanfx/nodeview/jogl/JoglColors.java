package com.pnambic.depanfx.nodeview.jogl;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.jogl.JoglTransforms;
import javafx.scene.paint.Color;

public class JoglColors {

  private JoglColors() {
    // Prevent instantiation.
  }

  public static DepanFxJoglColor of(Color color) {
    return new DepanFxJoglColor(
        color.getRed(), color.getGreen(), color.getBlue());
  }

  public static Color of(DepanFxJoglColor joglColor) {
    return Color.color(
        joglColor.getRed(), joglColor.getGreen(), joglColor.getBlue());
  }

  public static DepanFxJoglColor rgb(
      int redColor, int greenColor, int blueColor) {
    return new DepanFxJoglColor(
        JoglTransforms.colorByte(redColor),
        JoglTransforms.colorByte(greenColor),
        JoglTransforms.colorByte(blueColor));
  }
}
