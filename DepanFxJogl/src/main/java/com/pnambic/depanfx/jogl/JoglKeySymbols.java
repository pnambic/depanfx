package com.pnambic.depanfx.jogl;

import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.KeyEvent;

/**
 * OpenGL Key symbols to use outside of the Jogl module
 */
public class JoglKeySymbols {

  private JoglKeySymbols() {
    // Prevent instantiation.
  }

  public static final int CTRL_MASK = InputEvent.CTRL_MASK;
  public static final int EMPTY_MASK = 0;

  public static final short KS_HOME = KeyEvent.VK_HOME;
  public static final short KS_UP = KeyEvent.VK_UP;
  public static final short KS_DOWN = KeyEvent.VK_DOWN;
  public static final short KS_LEFT = KeyEvent.VK_LEFT;
  public static final short KS_RIGHT = KeyEvent.VK_RIGHT;
  public static final short KS_PAGE_UP = KeyEvent.VK_PAGE_UP;
  public static final short KS_PAGE_DOWN = KeyEvent.VK_PAGE_DOWN;

  public static final short KS_MINUS = KeyEvent.VK_MINUS;
  public static final short KS_PLUS = KeyEvent.VK_PLUS;

  // Letter key symbols for flight controls
  public static final short KS_W = KeyEvent.VK_W;
  public static final short KS_S = KeyEvent.VK_S;
  public static final short KS_A = KeyEvent.VK_A;
  public static final short KS_D = KeyEvent.VK_D;
  public static final short KS_Q = KeyEvent.VK_Q;
  public static final short KS_E = KeyEvent.VK_E;
  public static final short KS_R = KeyEvent.VK_R;
  public static final short KS_F = KeyEvent.VK_F;
  public static final short KS_X = KeyEvent.VK_X;
}
