package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.jogl.JoglTransforms;

import javafx.scene.paint.Color;

/**
 * A serializable version of JavaFX's Color.
 */
public class DepanFxJoglColor {

  private final double red;

  private final double green;

  private final double blue;

  public DepanFxJoglColor(double red, double green, double blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public static DepanFxJoglColor of(Color color) {
    return new DepanFxJoglColor(
        color.getRed(), color.getGreen(), color.getBlue());
  }

  public static DepanFxJoglColor rgb(
      int redColor, int greenColor, int blueColor) {
    return new DepanFxJoglColor(
        JoglTransforms.colorByte(redColor),
        JoglTransforms.colorByte(greenColor),
        JoglTransforms.colorByte(blueColor));
  }

  public double[] getColor() {
    return new double[] { red, green, blue };
  }

  public double getRed() {
    return red;
  }

  public double getGreen() {
    return green;
  }

  public double getBlue() {
    return blue;
  }

  public Color toFxColor() {
    return Color.color(
        getRed(), getGreen(), getBlue());
  }
}
