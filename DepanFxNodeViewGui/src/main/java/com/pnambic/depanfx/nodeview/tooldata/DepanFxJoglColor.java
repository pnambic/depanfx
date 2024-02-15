package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.jogl.JoglTransforms;

public class DepanFxJoglColor {

  private final float red;

  private final float green;

  private final float blue;

  public DepanFxJoglColor(float red, float green, float blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public static DepanFxJoglColor ofRGB(int red, int green, int blue) {
    return new DepanFxJoglColor(
        JoglTransforms.colorByte(red),
        JoglTransforms.colorByte(green),
        JoglTransforms.colorByte(blue));
  }

  public float[] getColor() {
    return new float[] { red, green, blue };
  }

  public float getRed() {
    return red;
  }

  public float getGreen() {
    return green;
  }

  public float getBlue() {
    return blue;
  }
}
