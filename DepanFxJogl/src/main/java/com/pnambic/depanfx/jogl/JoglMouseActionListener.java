package com.pnambic.depanfx.jogl;

import java.util.List;

public interface JoglMouseActionListener {

  void mouseDolly(double deltaX, double deltaY, double deltaZ);

  void moveSelection(double deltaX, double deltaY, double deltaZ);

  void rotateCamera(double f, double g, double h);

  void setSelection(List<Object> selection);

  void reduceSelection(List<Object> reduction);

  void extendSelection(List<Object> extension);
}
