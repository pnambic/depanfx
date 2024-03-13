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

  public interface KeyAction {
    boolean matches(KeyEvent event);
    void apply(KeyEvent event);
  }

  public static class SymbolAction implements KeyAction {
    private final short actionSymbol;

    private final int actionMods;

    private final BiConsumer<Short, Integer> keyAction;

    public SymbolAction(
        short actionSymbol, int actionMods,
        BiConsumer<Short, Integer> keyAction) {
      this.actionSymbol = actionSymbol;
      this.actionMods = actionMods;
      this.keyAction = keyAction;
    }

    @Override
    public boolean matches(KeyEvent event) {
      if (event.getKeySymbol() != actionSymbol) {
        return false;
      }
      if (event.getModifiers() != actionMods) {
        return false;
      }

      return true;
    }

    @Override
    public void apply(KeyEvent event) {
      keyAction.accept(event.getKeySymbol(), event.getModifiers());
      LOG.info("Applied action for key event {}", event);
    }
  }

  public static class CharAction implements KeyAction {
    private final char actionChar;

    private final BiConsumer<Character, Integer> keyAction;

    public CharAction(
        char actionChar,
        BiConsumer<Character, Integer> keyAction) {
      this.actionChar = actionChar;
      this.keyAction = keyAction;
    }

    @Override
    public boolean matches(KeyEvent event) {
      return event.getKeyChar() == actionChar;
    }

    @Override
    public void apply(KeyEvent event) {
      keyAction.accept(event.getKeyChar(), event.getModifiers());
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
    LOG.debug("Key press {}", event);
    performActions(event, pressActions);
  }

  @Override
  public void keyReleased(KeyEvent event) {
    LOG.debug("Key release {}", event);
    performActions(event, releaseActions);
  }

  private void performActions(KeyEvent event, Collection<KeyAction> actions) {
    actions.stream()
        .filter(a -> a.matches(event))
        .forEach(a -> a.apply(event));
  }
}
