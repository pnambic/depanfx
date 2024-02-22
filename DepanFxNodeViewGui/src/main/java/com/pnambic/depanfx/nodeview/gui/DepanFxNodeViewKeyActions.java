package com.pnambic.depanfx.nodeview.gui;

import static com.pnambic.depanfx.jogl.JoglKeySymbols.*;

import com.pnambic.depanfx.jogl.JoglModule;
import com.pnambic.depanfx.jogl.JoglKeyListener.CharAction;
import com.pnambic.depanfx.jogl.JoglKeyListener.SymbolAction;

public class DepanFxNodeViewKeyActions {

  public static final double ZERO_MOVE_D = 0.0d;

  public static final double UNIT_MOVE_D = 1.0d;

  public static final double UNIT_TURN_D = 10.0d; // Degrees

  public static final double ZERO_AXIS_D = 0.0d;

  public static final double UNIT_AXIS_D = 1.0d;

  public static final double ZOOM_IN_D = 0.9d;

  public static final double ZOOM_OUT_D = 1.10d;

  public static final char ZOOM_IN_CHAR = '+';

  private DepanFxNodeViewKeyActions() {
    // Prevent instantiation.
  }

  public static void addActions(JoglModule jogl, CameraChangeListener listener) {
    jogl.addPressAction(
        new SymbolAction(KS_HOME, EMPTY_MASK,
            (s, m) -> listener.home()));
    jogl.addPressAction(
        new SymbolAction(KS_UP, EMPTY_MASK,
            (s, m) -> listener.dolly(ZERO_MOVE_D, UNIT_MOVE_D, ZERO_MOVE_D)));
    jogl.addPressAction(
        new SymbolAction(KS_DOWN, EMPTY_MASK,
            (s, m) -> listener.dolly(ZERO_MOVE_D, -UNIT_MOVE_D, ZERO_MOVE_D)));
    jogl.addPressAction(
        new SymbolAction(KS_LEFT, EMPTY_MASK,
            (s, m) -> listener.dolly(UNIT_MOVE_D, ZERO_MOVE_D, ZERO_MOVE_D)));
    jogl.addPressAction(
        new SymbolAction(KS_RIGHT, EMPTY_MASK,
            (s, m) -> listener.dolly(-UNIT_MOVE_D, ZERO_MOVE_D, ZERO_MOVE_D)));
    jogl.addPressAction(
        new SymbolAction(KS_PAGE_UP, EMPTY_MASK,
            (s, m) -> listener.dolly(ZERO_MOVE_D, ZERO_MOVE_D, -UNIT_MOVE_D)));
    jogl.addPressAction(
        new SymbolAction(KS_PAGE_DOWN, EMPTY_MASK,
            (s, m) -> listener.dolly(ZERO_MOVE_D, ZERO_MOVE_D, UNIT_MOVE_D)));

    // Tilt: rotate about the X axis
    jogl.addPressAction(
        new SymbolAction(KS_UP, CTRL_MASK,
            (s, m) -> listener.rotate(
                -UNIT_TURN_D, UNIT_AXIS_D, ZERO_AXIS_D, ZERO_AXIS_D)));
    jogl.addPressAction(
        new SymbolAction(KS_DOWN, CTRL_MASK,
            (s, m) -> listener.rotate(
                UNIT_TURN_D, UNIT_AXIS_D, ZERO_AXIS_D, ZERO_AXIS_D)));

    // Pan: rotate about the Y axis
    jogl.addPressAction(
        new SymbolAction(KS_LEFT, CTRL_MASK,
            (s, m) -> listener.rotate(
                -UNIT_TURN_D, ZERO_AXIS_D, UNIT_AXIS_D, ZERO_AXIS_D)));
    jogl.addPressAction(
        new SymbolAction(KS_RIGHT, CTRL_MASK,
            (s, m) -> listener.rotate(
                UNIT_TURN_D, ZERO_AXIS_D, UNIT_AXIS_D, ZERO_AXIS_D)));

    // Move: forward and backward on line of sight
    jogl.addPressAction(
        new SymbolAction(KS_PAGE_UP, CTRL_MASK,
            (s, m) -> listener.move(UNIT_MOVE_D)));
    jogl.addPressAction(
        new SymbolAction(KS_PAGE_DOWN, CTRL_MASK,
            (s, m) -> listener.move(-UNIT_MOVE_D)));

    // Zoom: narrow or widen the field of view.
    jogl.addPressAction(
        new SymbolAction(KS_PLUS, EMPTY_MASK,
            (s, m) -> listener.zoom(ZOOM_IN_D)));
    jogl.addPressAction(
        new CharAction(ZOOM_IN_CHAR,
            (c, m) -> listener.zoom(ZOOM_IN_D)));
    jogl.addPressAction(
        new SymbolAction(KS_MINUS, EMPTY_MASK,
            (s, m) -> listener.zoom(ZOOM_OUT_D)));
  }
}
