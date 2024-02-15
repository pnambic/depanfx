package com.pnambic.depanfx.jogl;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

public class JoglKeyListener implements KeyListener {

  private final static Logger LOG =
      LoggerFactory.getLogger(JoglKeyListener.class);

  public static class KeyAction {
    private final short actionSymbol;

    private final int actionMods;

    private final BiConsumer<Short, Integer> keyAction;

    public KeyAction(
        short actionSymbol, int actionMods,
        BiConsumer<Short, Integer> keyAction) {
      this.actionSymbol = actionSymbol;
      this.actionMods = actionMods;
      this.keyAction = keyAction;
    }

    public boolean matches(KeyEvent event) {
      if (event.getKeySymbol() != actionSymbol) {
        return false;
      }
      if (event.getModifiers() != actionMods) {
        return false;
      }

      return true;
    }

    public void apply(KeyEvent event) {
      keyAction.accept(event.getKeySymbol(), event.getModifiers());
      LOG.info("Applied action for key event {}", event);
    }
  }

  private final Collection<KeyAction> pressActions = new ArrayList<>();

  private final Collection<KeyAction> releaseActions = new ArrayList<>();

  public void addPressAction(KeyAction keyAction) {
    pressActions.add(keyAction);
  }

  public void addReleaseAction(KeyAction keyAction) {
    releaseActions.add(keyAction);
  }

  @Override
  public void keyPressed(KeyEvent event) {
    performActions(event, pressActions);
  }

  @Override
  public void keyReleased(KeyEvent event) {
    performActions(event, releaseActions);
  }

  private void performActions(KeyEvent event, Collection<KeyAction> actions) {
    actions.stream()
        .filter(a -> a.matches(event))
        .forEach(a -> a.apply(event));
  }
}
