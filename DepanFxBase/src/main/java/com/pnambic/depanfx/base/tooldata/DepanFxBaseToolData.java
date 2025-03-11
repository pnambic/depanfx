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
package com.pnambic.depanfx.base.tooldata;

import java.util.Comparator;

/**
 * The minimal expectations for any tool data.
 */
public class DepanFxBaseToolData {

  /**
   * Provides an alphabetically ordered sequence of filter resources,
   * based on each tool's name.
   *
   * This ensure that consumers always see the same order,
   * regardless of set construction.
   */
  public static final Comparator<DepanFxBaseToolData> BY_TOOL_NAME =
      (l, r) -> l.getToolName().compareTo(r.getToolName());

  private final String toolName;

  private final String toolDescription;

  public DepanFxBaseToolData(String toolName, String toolDescription) {
    this.toolName = toolName;
    this.toolDescription = toolDescription;
  }

  public String getToolName() {
    return toolName;
  }

  public String getToolDescription() {
    return toolDescription;
  }
}
