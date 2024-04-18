/*
 * Copyright 2008, 2024 The Depan Project Authors
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

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class JoglMouseListener implements MouseListener {

  private static final Logger LOG =
      LoggerFactory.getLogger(JoglMouseListener.class);

  private final JoglRenderer renderer;

  /** Modifier keys states. */
  private final JoglKeyListener keyListener;

  private boolean keyAltState = false;

  private boolean keyCtrlState = false;

  private boolean keyShiftState = false;

  /** mouse position at last mouseDown event */
  int anchorX = -1;

  int anchorY = -1;

  private enum State {
    None, Moving, MovingObject, RectangleSelection
  }

  private State state = State.None;

  private JoglMouseActionListener actionListener;

  public JoglMouseListener(JoglRenderer renderer, JoglKeyListener keyListener) {
    this.renderer = renderer;
    this.keyListener = keyListener;
    prepareKeyListener();
  }

  public void addMouseActionListener(JoglMouseActionListener actionListener) {
    // TODO: allow multiple action listeners?  When the need arises.
    this.actionListener = actionListener;
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    LOG.debug("mouse clicked {} type {}",
        event.getClass().getName(), event.getEventType());
  }

  @Override
  public void mouseEntered(MouseEvent event) {
    LOG.debug("mouse entered event {} type {}",
        event.getClass().getName(), event.getEventType());
  }

  @Override
  public void mouseExited(MouseEvent event) {
    LOG.debug("mouse exited {} type {}",
        event.getClass().getName(), event.getEventType());
  }

  @Override
  public void mouseDragged(MouseEvent event) {
    LOG.debug("mouse dragged event {} type {}",
        event.getClass().getName(), event.getEventType());

    int deltaX = anchorX - event.getX();
    int deltaY = anchorY - event.getY();
    double scaleX = renderer.scaleMouseX(deltaX);
    double scaleY = renderer.scaleMouseY(deltaY);

    if (event.getButton() == 1) { // button1 pressed
      switch (state) {
      case Moving:
        actionListener.mouseDolly(scaleX, -scaleY, 0);

        anchorX = event.getX();
        anchorY = event.getY();
        break;
      case MovingObject:
        actionListener.moveSelection(scaleX, scaleY, 0);
        // scene.moveSelectedObjectsTo(deltaX, deltaY);
        break;
      case RectangleSelection:
        int viewportHeight = renderer.getViewportHeight();

        renderer.activateSelectionRectangle(
            renderer.scaleMouseX(anchorX),
            renderer.scaleMouseY(viewportHeight - anchorY),
            renderer.scaleMouseX(event.getX()),
            renderer.scaleMouseY(viewportHeight - event.getY()));
        break;
      default:
      }
    }
    if (event.getButton() == 2) {
      switch (state) {
      case Moving:
        actionListener.rotateCamera(-deltaY / 10f, 0.0f, deltaX / 10f);
        break;
      default:
        // Explicitly ignore other state
        break;
      }
    }
  }

  @Override
  public void mouseMoved(MouseEvent event) {
    LOG.debug("mouse moved to ({}, {})", event.getX(), event.getY());
  }

  @Override
  public void mousePressed(MouseEvent event) {
    LOG.info("mouse pressed button {})", event.getButton());

    anchorX = event.getX();
    anchorY = event.getY();

    int[] hits = getHits();

    // The user clicked on an object without control or shift
    // Entry move mode, and make the picked node the select node if it
    // is not part of the current selection.
    if (hits.length > 0 && !keyCtrlState && !keyShiftState) {
      actionListener.setSelection(renderer.pickObjectsAt(anchorX, anchorY));
      state = State.MovingObject;
    }

    // Start a rectangle selection
    else if (hits.length > 0 && keyCtrlState) {
      state = State.RectangleSelection;
    }

    // Clicked with shift: start selection area
    else if (keyShiftState) {
      state = State.RectangleSelection;

    // Clicked on the background: start moving camera
    } else {
      state = State.Moving;
    }
    LOG.info("mouse state is {})", state);
  }

  @Override
  public void mouseReleased(MouseEvent event) {
    LOG.info("mouse release button {} state ()", event.getButton(), state);

    int eventX = event.getX();
    int eventY = event.getY();

    if (event.getButton() == 1) {
      renderer.releaseSelectionRectangle();
      if (anchorX == eventX && anchorY == eventY
          && state == State.MovingObject) {
        // instead of moving the node, the mouse stayed at the same place.
        // we replace the selection.
        actionListener.setSelection(
            renderer.pickObjectsAt(eventX, eventY));
      }
      else if (anchorX == eventX && anchorY == eventY
          && state == State.RectangleSelection) {
        // a rectangle when the mouse hasn't moved... select nothing
        if (!keyCtrlState) {
          actionListener.setSelection(Collections.emptyList());
        }
        else if (keyAltState) {
          actionListener.reduceSelection(
              renderer.pickObjectsAt(eventX, eventY));
        }
        else {
          actionListener.extendSelection(
              renderer.pickObjectsAt(eventX, eventY));
        }
      }
      else if (state == State.RectangleSelection) {
        List<Object> picked =
            renderer.pickObjectsIn(anchorX, anchorY, eventX, eventY);
        if (keyCtrlState) {
          actionListener.extendSelection(picked);
        }
        else if (keyAltState) {
          actionListener.reduceSelection(picked);
        }
        else
          actionListener.setSelection(picked);
      }
    }
    state = State.None;
  }

  @Override
  public void mouseWheelMoved(MouseEvent event) {
    LOG.debug("mouse wheel {}", event.getEventType());

    // A wheel is a y-axis rotation, so use the second element in the array.
    float amount = event.getRotation()[1] * event.getRotationScale();
    actionListener.mouseDolly(0, 0, amount);
  }

  /////////////////////////////////////

  private void prepareKeyListener() {
    keyListener.addPressAction(new KeyPressAction());
    keyListener.addReleaseAction(new KeyPressAction());
  }

  private int[] getHits() {
    // LEGACY: scene.pickObjectsAt(mouseDownX, mouseDownY);
    return new int[0];
  }

  private class KeyPressAction implements JoglKeyListener.KeyAction {

    @Override
    public boolean matches(KeyEvent event) {
      switch (event.getKeyCode()) {
      case KeyEvent.VK_ALT:
      case KeyEvent.VK_CONTROL:
      case KeyEvent.VK_SHIFT:
        return true;
      }
      return false;
    }

    @Override
    public void apply(KeyEvent event) {
      LOG.info("Applying event type {} for key code {}",
          event.getEventType(), event.getKeyCode());
      // Confirm the expected event
      boolean pressEvent = event.getEventType() == 300;
      boolean releaseType = event.getEventType() == 301;
      if (!pressEvent && !releaseType) {
        return;
      }
      boolean keyState = pressEvent;
      switch (event.getKeyCode()) {
      case KeyEvent.VK_ALT:
        keyAltState = keyState;
        return;
      case KeyEvent.VK_CONTROL:
        keyCtrlState = keyState;
        return;
      case KeyEvent.VK_SHIFT:
        keyShiftState = keyState;
        return;
      }
    }
  }
}
