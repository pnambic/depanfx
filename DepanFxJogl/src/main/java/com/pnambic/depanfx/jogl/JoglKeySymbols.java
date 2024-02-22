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
}
