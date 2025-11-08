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

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

public class DepanFxTreeLayoutData extends DepanFxBaseToolData {

  public static final String TREE_LAYOUT_TOOL_EXT = "dtlti";

  public static enum Direction {
    RIGHT,
    LEFT,
    UP,
    DOWN
  }

  private final Direction direction;;

  private final DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>
      linkMatcherRsrc;

  public DepanFxTreeLayoutData(
      String toolName, String toolDescription,
      Direction direction,
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> linkMatcherRsrc) {
    super(toolName, toolDescription);
    this.direction = direction;
    this.linkMatcherRsrc = linkMatcherRsrc;
  }

  public DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>
      getHierarchyMatcherRsrc() {

    return linkMatcherRsrc;
  }

  public Direction getDirection() {
    return direction;
  }
}
