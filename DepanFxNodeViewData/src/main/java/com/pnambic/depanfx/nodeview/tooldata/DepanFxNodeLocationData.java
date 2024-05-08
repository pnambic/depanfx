package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.info.GraphNodeInfo;

public class DepanFxNodeLocationData implements GraphNodeInfo {

  public double xPos;

  public double yPos;

  public double zPos;

  public DepanFxNodeLocationData(double xPos, double yPos, double zPos) {
    this.xPos = xPos;
    this.yPos = yPos;
    this.zPos = zPos;
  }

  public static DepanFxNodeLocationData shift(
      DepanFxNodeLocationData base,
      double shiftX, double shiftY, double shiftZ) {
    return new DepanFxNodeLocationData(
        base.xPos + shiftX, base.yPos + shiftY, base.zPos + shiftZ);
  }
}
