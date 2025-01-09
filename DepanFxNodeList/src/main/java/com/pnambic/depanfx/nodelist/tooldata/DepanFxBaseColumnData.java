/*
 * Copyright 2025 The Depan Project Authors
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
package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

public class DepanFxBaseColumnData extends DepanFxBaseToolData {

  public static final String BASE_COLUMN_NAME = "Column";

  public static final int BASE_COLUMN_WIDTH_MS = 12;

  private final String columnLabel;

  private final int widthMs;

  public DepanFxBaseColumnData(
      String toolName, String toolDescription,
      String columnLabel, int widthMs) {

    super(toolName, toolDescription);
    this.columnLabel = columnLabel;
    this.widthMs = widthMs;
  }

  public String getColumnLabel() {
    return columnLabel;
  }

  public int getWidthMs() {
    return widthMs;
  }
}
