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

import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

public class DepanFxShiftLayoutData extends DepanFxBaseToolData {

  public static final String SHIFT_LAYOUT_TOOL_EXT = "dslti";

  private final double shiftX;

  private final double shiftY;

  private final double shiftZ;

  public DepanFxShiftLayoutData(
      String toolName, String toolDescription,
      double shiftX, double shiftY, double shiftZ) {
    super(toolName, toolDescription);
    this.shiftX = shiftX;
    this.shiftY = shiftY;
    this.shiftZ = shiftZ;
  }

  public double getShiftX() {
    return shiftX;
  }

  public double getShiftY() {
    return shiftY;
  }

  public double getShiftZ() {
    return shiftZ;
  }
}
