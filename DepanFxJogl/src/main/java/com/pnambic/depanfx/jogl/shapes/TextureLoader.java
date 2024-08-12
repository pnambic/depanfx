package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class TextureLoader {

  private static final int BORDER_WIDTH = 0;

  private static final int BUFFER_WIDTH = 256;

  private static final int BUFFER_HEIGHT = 64;

  private static final int BYTES_PER_INT = 4;

  private final int textureId;

  TextureLoader(GL2 gl) {
      // Generate texture id
      int[] ids = new int[1];
      gl.glGenTextures(1, ids, 0);
      textureId = ids[0];
  }

  void loadTexture(String text, Color color) {
      BufferedImage image =
          new BufferedImage(256, 64, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = image.createGraphics();
      g2d.setColor(color);
      g2d.drawString(text, 10, 40);
      g2d.dispose();

      ByteBuffer buffer = Buffers.newDirectByteBuffer(
          BUFFER_WIDTH * BUFFER_HEIGHT * BYTES_PER_INT);
      for (int y = 0; y < BUFFER_HEIGHT; y++) {
          for (int x = 0; x < BUFFER_WIDTH; x++) {
              int argb = image.getRGB(x, y);
              buffer.putInt(argb << 8 | argb >>> 24);
          }
      }
      buffer.flip();

      GL2 gl = GLContext.getCurrentGL().getGL2();
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
      gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA,
          BUFFER_WIDTH, BUFFER_HEIGHT, BORDER_WIDTH,
          GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
  }

  void draw(GL2 gl, double top, double left, double bottom, double right) {
      gl.glEnable(GL.GL_TEXTURE_2D);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId);
      gl.glBegin(GL2.GL_QUADS);
      gl.glTexCoord2f(0, 0);
      gl.glVertex2d(left, top);
      gl.glTexCoord2f(1, 0);
      gl.glVertex2d(right, top);
      gl.glTexCoord2f(1, 1);
      gl.glVertex2d(right, bottom);
      gl.glTexCoord2f(0, 1);
      gl.glVertex2d(left, bottom);
      gl.glEnd();
      gl.glDisable(GL.GL_TEXTURE_2D);
  }

  void dispose() {
      GL2 gl = GLContext.getCurrentGL().getGL2();
      int[] ids = {textureId};
      gl.glDeleteTextures(1, ids, 0);
  }
}
