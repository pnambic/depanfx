/*
 * Copyright 2024 The Depan Project Authors
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

  public static DepanFxNodeLocationData buildPoint() {
    return new DepanFxNodeLocationData(0.0d, 0.0d, 0.0d);
  }

  public static DepanFxNodeLocationData shift(
      DepanFxNodeLocationData base,
      double shiftX, double shiftY, double shiftZ) {
    return new DepanFxNodeLocationData(
        base.xPos + shiftX, base.yPos + shiftY, base.zPos + shiftZ);
  }

  public static DepanFxNodeLocationData calcDelta(
      DepanFxNodeLocationData base,
      DepanFxNodeLocationData target) {
    return new DepanFxNodeLocationData(
        target.xPos - base.xPos,
        target.yPos - base.yPos,
        target.zPos - base.zPos);
  }

  public static DepanFxNodeLocationData applyDelta(
      DepanFxNodeLocationData base,
      DepanFxNodeLocationData delta) {
    return new DepanFxNodeLocationData(
        base.xPos + delta.xPos,
        base.yPos + delta.yPos,
        base.zPos + delta.zPos);
  }
}
