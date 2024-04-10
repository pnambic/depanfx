package com.pnambic.depanfx.nodeview.tooldata;

import javafx.scene.paint.Color;

/**
 * A serializable version of JavaFX's Color.
 */
public class DepanFxJoglColor {

  // Minimal number of "well-known" colors.
  // Mostly, UI aware components should use native color definitions
  // and map those colors into this storage format.
  public static final DepanFxJoglColor BLACK =
      new DepanFxJoglColor(0.0, 0.0, 0.0);

  public static final DepanFxJoglColor WHITE =
      new DepanFxJoglColor(0.0, 0.0, 0.0);

  private final double red;

  private final double green;

  private final double blue;

  public DepanFxJoglColor(double red, double green, double blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  /**
   * Recommended source of color definitions.
   */
  public static DepanFxJoglColor of(Color color) {
    return new DepanFxJoglColor(
        color.getRed(), color.getGreen(), color.getBlue());
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
}
