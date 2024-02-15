package com.pnambic.depanfx.nodeview.gui;

import static com.pnambic.depanfx.jogl.JoglKeySymbols.*;

import com.pnambic.depanfx.jogl.JoglModule;
import com.pnambic.depanfx.jogl.JoglKeyListener.KeyAction;

public class DepanFxNodeViewKeyActions {

  private DepanFxNodeViewKeyActions() {
    // Prevent instantiation.
  }

  public static void addActions(JoglModule jogl, DepanFxJoglView view) {
    jogl.addPressAction(
        new KeyAction(KS_UP, 0,
            (s, m) -> view.dolly(0, 1, 0)));
    jogl.addPressAction(
        new KeyAction(KS_DOWN, 0,
            (s, m) -> view.dolly(0, -1, 0)));
    jogl.addPressAction(
        new KeyAction(KS_LEFT, 0,
            (s, m) -> view.dolly(1, 0, 0)));
    jogl.addPressAction(
        new KeyAction(KS_RIGHT, 0,
            (s, m) -> view.dolly(-1, 0, 0)));
    jogl.addPressAction(
        new KeyAction(KS_PAGE_UP, 0,
            (s, m) -> view.dolly(0, 0, -1)));
    jogl.addPressAction(
        new KeyAction(KS_PAGE_DOWN, 0,
            (s, m) -> view.dolly(0, 0, 1)));
  }
}
