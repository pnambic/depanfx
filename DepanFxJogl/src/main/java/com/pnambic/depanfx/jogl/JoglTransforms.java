package com.pnambic.depanfx.jogl;

public class JoglTransforms {

  public static final int COLOR_BYTE_MIN = 0;

  public static final int COLOR_BYTE_MAX = 255;

  private JoglTransforms() {
    // Prevent instantiation.
  }

  public static float colorByte(int color) {
    if (color <= COLOR_BYTE_MIN) {
      return 0.0f;
    }
    if (color >= COLOR_BYTE_MAX) {
      return 0.0f;
    }
    return ((float) color) / ((float) COLOR_BYTE_MAX);
  }
}
