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

public class DepanFxNodeKeyColumnData extends DepanFxBaseColumnData {

  public static final String NODE_KEY_COLUMN_TOOL_EXT = "dnkcti";

  public static final String BASE_COLUMN_LABEL = "Key";

  public enum KeyChoice { MODEL_KEY, KIND_KEY, NODE_KEY, SIMPLE_NAME }

  private final KeyChoice keyChoice;

  public DepanFxNodeKeyColumnData(
      String toolName, String toolDescription,
      String columnLabel, int widthMs, KeyChoice keyChoice) {

    super(toolName, toolDescription, columnLabel, widthMs);
    this.keyChoice = keyChoice;
  }

  public KeyChoice getKeyChoice() {
    return keyChoice;
  }
}
