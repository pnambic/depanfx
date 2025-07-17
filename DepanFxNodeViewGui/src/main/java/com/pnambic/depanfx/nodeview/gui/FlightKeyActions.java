package com.pnambic.depanfx.nodeview.gui;

import static com.pnambic.depanfx.jogl.JoglKeySymbols.*;

import com.pnambic.depanfx.jogl.JoglModule;
import com.pnambic.depanfx.jogl.JoglKeyListener.SymbolAction;

/**
 * Install keyboard actions for flight control.
 */
public class FlightKeyActions {

  private FlightKeyActions() {
    // Prevent instantiation.
  }

  public static void addActions(JoglModule jogl, FlightControl flight) {
    jogl.addPressAction(new SymbolAction(KS_W, EMPTY_MASK,
        (s, m) -> flight.pitchDown()));
    jogl.addPressAction(new SymbolAction(KS_S, EMPTY_MASK,
        (s, m) -> flight.pitchUp()));
    jogl.addPressAction(new SymbolAction(KS_A, EMPTY_MASK,
        (s, m) -> flight.rollLeft()));
    jogl.addPressAction(new SymbolAction(KS_D, EMPTY_MASK,
        (s, m) -> flight.rollRight()));
    jogl.addPressAction(new SymbolAction(KS_Q, EMPTY_MASK,
        (s, m) -> flight.yawLeft()));
    jogl.addPressAction(new SymbolAction(KS_E, EMPTY_MASK,
        (s, m) -> flight.yawRight()));
    jogl.addPressAction(new SymbolAction(KS_R, EMPTY_MASK,
        (s, m) -> flight.increaseThrottle()));
    jogl.addPressAction(new SymbolAction(KS_F, EMPTY_MASK,
        (s, m) -> flight.decreaseThrottle()));
    jogl.addPressAction(new SymbolAction(KS_X, EMPTY_MASK,
        (s, m) -> flight.cutThrottle()));
  }
}

