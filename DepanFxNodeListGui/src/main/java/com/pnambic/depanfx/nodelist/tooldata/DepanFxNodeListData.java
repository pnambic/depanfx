/*
 * Copyright 2023 The Depan Project Authors
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

import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.io.File;
import java.nio.file.Path;

/**
 * Common data and paths for node list section tool data.
 *
 * Possibly provide sharable field validation methods in the future.
 */
public class DepanFxNodeListData {

  public static final String NODE_LIST_DIR = "Node List";

  public static final Path NODE_LIST_TOOL_PATH =
      DepanFxProjects.TOOLS_PATH.resolve(NODE_LIST_DIR);

  public static File buildCurrentToolFile(
      Path parentPath, String dataLabel, String ext) {
    String toolName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        dataLabel, ext);
    return parentPath.resolve(toolName).toFile();
  }
}
