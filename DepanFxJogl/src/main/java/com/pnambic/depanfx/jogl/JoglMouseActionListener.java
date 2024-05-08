package com.pnambic.depanfx.jogl;

import java.util.List;

public interface JoglMouseActionListener {

  /**
   * Move the camera by the indicated amounts.
   */
  void mouseDolly(double deltaX, double deltaY, double deltaZ);

  void rotateCamera(double f, double g, double h);

  /**
   * Move the nodes of the current selection by the indicated amounts.
   */
  void moveSelection(double deltaX, double deltaY, double deltaZ);

  /**
   * Set the current selection to the supplied list.
   */
  void setSelection(List<Object> selection);

  /**
   * Remove the supplied list from the current selection.
   */
  void reduceSelection(List<Object> reduction);

  /**
   * Add the supplied list tothe current selection.
   */
  void extendSelection(List<Object> extension);

  /**
   * If current selection includes all elements of the supplied list,
   * leave the current selection.  Otherwise, set the current selection
   * to the supplied list.
   */
  void reviseSelection(List<Object> extension);
}
