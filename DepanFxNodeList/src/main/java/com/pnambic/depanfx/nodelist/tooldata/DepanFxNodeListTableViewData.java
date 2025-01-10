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

import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DepanFxNodeListTableViewData extends DepanFxBaseToolData {

  public static final String TABLE_VIEWS_TOOL_DIR = "Table Views";

  public static final String TABLE_VIEW_TOOL_EXT = "dtvti";

  public static final String AS_MEMBER_VIEW_CONTEXT_NAME = "As Member View";

  public static final Path TABLE_VIEW_TOOL_PATH =
      DepanFxNodeListData.NODE_LIST_TOOL_PATH.resolve(TABLE_VIEWS_TOOL_DIR);

  private final List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>> sectionResources;

  private final List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>> columnResources;

  public DepanFxNodeListTableViewData(
      String toolName, String toolDescription,
      List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>> sectionResources,
      List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>> columnResources) {

    super(toolName, toolDescription);

    // Safety, and to ensure serializablity.
    // (even in the face of a supplied Collections.emtpyList()).
    this.sectionResources = new ArrayList<>(sectionResources);
    this.columnResources = new ArrayList<>(columnResources);
  }

  public List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
      getSectionResources() {
    return sectionResources;
  }

  public List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
      getColumnResources() {
    return columnResources;
  }
}
