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
package com.pnambic.depanfx.nodefilters.tooldata;

import com.pnambic.depanfx.workspace.projects.DepanFxProjects;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.nio.file.Path;

public class DepanFxBaseFilterData extends DepanFxBaseToolData {

  private static final String NODE_FILTERS_TOOL_DIR = "Filters";

  public static final String NODE_VSIBILITY_CONTEXT_RESOURCE_NAME = "Node Visibility";

  public static final Path NODE_FILTERS_TOOL_PATH =
      DepanFxProjects.TOOLS_PATH.resolve(NODE_FILTERS_TOOL_DIR);

  private final FilterMergeMode mergeMode;

  public DepanFxBaseFilterData(
      String toolName, String toolDescription, FilterMergeMode mergeMode) {
    super(toolName, toolDescription);
    this.mergeMode = mergeMode;
  }

  public FilterMergeMode getMergeMode() {
    return mergeMode;
  }
}
