package com.pnambic.depanfx.jogl;

/**
 * A color type that is independent of JavaFx.
 *
 * Compatible with JavaFx color and DepanFx serialization color.
 *
 * OpenGL's color primitive's tends to be {@code float}, so explicit downcasts
 * are required at the final point of use.
 */
public class JoglColor {

  public double red;

  public double green;

  public double blue;

  public JoglColor(double red, double green, double blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }
}
