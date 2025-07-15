/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Off-screen buffer used for color based picking.
 *
 * This class manages a framebuffer object (FBO) that can be used to render
 * shapes into a texture, which can then be read back to determine which shape
 * was clicked on.
 *
 * Render into off screen buffer so that selection render does not disturb
 * the visible frame.
 */
public class JoglPickBuffer {

  private static final Logger LOG =
      LoggerFactory.getLogger(JoglPickBuffer.class);

  // Off screen buffer used for color based picking
  private int pickFramebuffer;

  private int pickWidth;

  private int pickHeight;

  // Internal resources
  private int pickColorTex;

  private int pickDepthBuf;

  public JoglPickBuffer() {
    this.pickFramebuffer = 0;
    this.pickColorTex = 0;
    this.pickDepthBuf = 0;
    this.pickWidth = 0;
    this.pickHeight = 0;
  }

  public void dispose(GL2 gl) {
    int[] ids = new int[1];
    if (pickColorTex != 0) {
      ids[0] = pickColorTex;
      gl.glDeleteTextures(1, ids, 0);
      pickColorTex = 0;
    }
    if (pickDepthBuf != 0) {
      ids[0] = pickDepthBuf;
      gl.glDeleteRenderbuffers(1, ids, 0);
      pickDepthBuf = 0;
    }
    if (pickFramebuffer != 0) {
      ids[0] = pickFramebuffer;
      gl.glDeleteFramebuffers(1, ids, 0);
      pickFramebuffer = 0;
    }
  }

  /**
   * Support lazy initialization of the frame buffer for picking.
   */
  public void use(GL2 gl, int viewportWidth, int viewportHeight) {
    if (pickFramebuffer == 0) {
      install(gl, viewportWidth, viewportHeight);
    }
    else if (pickWidth != viewportWidth || pickHeight != viewportHeight) {
      dispose(gl);
      install(gl, viewportWidth, viewportHeight);
    }

    bind(gl);
  }

  public void bind(GL2 gl) {
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, pickFramebuffer);
  }

  public void release(GL gl) {
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
  }

  /**
   * Install the framebuffer used for color based picking.
   */
  private void install(GL2 gl, int width, int height) {
    int[] ids = new int[1];

    gl.glGenTextures(1, ids, 0);
    pickColorTex = ids[0];
    gl.glBindTexture(GL.GL_TEXTURE_2D, pickColorTex);
    gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_RGBA8,
        width, height, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
        GL.GL_NEAREST);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
        GL.GL_NEAREST);

    gl.glGenRenderbuffers(1, ids, 0);
    pickDepthBuf = ids[0];
    gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, pickDepthBuf);
    gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT24,
        width, height);

    gl.glGenFramebuffers(1, ids, 0);
    pickFramebuffer = ids[0];
    if (pickFramebuffer == 0) {
      LOG.warn("Gen Frameburrers reports error {}", gl.glGetError());
    }

    bind(gl);
    gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0,
        GL.GL_TEXTURE_2D, pickColorTex, 0);
    gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT,
        GL.GL_RENDERBUFFER, pickDepthBuf);

    int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
    if (status != GL.GL_FRAMEBUFFER_COMPLETE) {
      LOG.warn("Pick framebuffer incomplete: {} error {}",
          status, gl.glGetError());
    }
    release(gl);

    pickWidth = width;
    pickHeight = height;
  }
}
