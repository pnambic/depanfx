package com.pnambic.depanfx.jogl;

import java.awt.Color;

/**
 * A color type that is independent of JavaFx.
 *
 * Compatible with JavaFx color and DepanFx serialization color.
 *
 * OpenGL's color primitive's tends to be {@code float}, so explicit downcasts
 * are required at the final point of use.
 */
public class JoglColor {

  public static double NONE = 0.0d;

  public static double FULL = 1.0d;

  public double red;

  public double green;

  public double blue;

  public JoglColor(double red, double green, double blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public JoglColor complement() {
    return new JoglColor(
        FULL - red, FULL - green, FULL- blue);
  }

  public JoglColor shift(double shiftBy) {
    return new JoglColor(
        red * shiftBy, green * shiftBy, blue * shiftBy);
  }

  public Color toAwtColor() {
    return new Color((float) red, (float) green, (float) blue);
  }
}
