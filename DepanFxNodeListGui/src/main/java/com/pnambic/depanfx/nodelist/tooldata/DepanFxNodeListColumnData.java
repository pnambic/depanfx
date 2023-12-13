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

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Common data and paths for node list section tool data.
 *
 * Possibly provide sharable field validation methods in the future.
 */
public class DepanFxNodeListColumnData {

  public static final String COLUMNS_TOOL_DIR = "Columns";

  public static final String SIMPLE_COLUMN_NAME = "Simple Column";

  public static final String MODEL_KEY_COLUMN_NAME = "Model Key Column";

  public static final String KIND_KEY_COLUMN_NAME = "Kind Key Column";

  public static final String NODE_KEY_COLUMN_NAME = "Node Column";

  public static final Path COLUMNS_TOOL_PATH =
      DepanFxNodeListData.NODE_LIST_TOOL_PATH.resolve(COLUMNS_TOOL_DIR);

  public static final Path SIMPLE_COLUMN_TOOL_PATH =
      COLUMNS_TOOL_PATH.resolve(SIMPLE_COLUMN_NAME);

  public static final Path MODEL_KEY_COLUMN_TOOL_PATH =
      COLUMNS_TOOL_PATH.resolve(KIND_KEY_COLUMN_NAME);

  public static final Path KIND_KEY_COLUMN_TOOL_PATH =
      COLUMNS_TOOL_PATH.resolve(SIMPLE_COLUMN_NAME);

  public static final Path NODE_KEY_COLUMN_TOOL_PATH =
      COLUMNS_TOOL_PATH.resolve(NODE_KEY_COLUMN_NAME);

  private DepanFxNodeListColumnData() {
    // Prevent instantiation.
  }

  public static Optional<DepanFxWorkspaceResource> getBuiltinSimpleColumnResource(
      DepanFxWorkspace workspace) {
    return getBuiltinResource(
        workspace, DepanFxSimpleColumnData.class, SIMPLE_COLUMN_TOOL_PATH);
  }

  public static Optional<DepanFxWorkspaceResource> getBuiltinNodeKeyColumnResource(
      DepanFxWorkspace workspace, KeyChoice keyChoice) {
    switch (keyChoice) {
    case MODEL_KEY:
      return getBuiltinModelKeyColumnResource(workspace);
    case KIND_KEY:
      return getBuiltinKindKeyColumnResource(workspace);
    case NODE_KEY:
      return getBuiltinNodeKeyColumnResource(workspace);
    default:
      throw new IllegalArgumentException("Unexpected value: " + keyChoice);
    }
  }

  public static Optional<DepanFxWorkspaceResource> getBuiltinModelKeyColumnResource(
      DepanFxWorkspace workspace) {
    return getBuiltinResource(
        workspace, DepanFxNodeKeyColumnData.class, MODEL_KEY_COLUMN_TOOL_PATH);
  }

  public static Optional<DepanFxWorkspaceResource> getBuiltinKindKeyColumnResource(
      DepanFxWorkspace workspace) {
    return getBuiltinResource(
        workspace, DepanFxNodeKeyColumnData.class, KIND_KEY_COLUMN_TOOL_PATH);
  }

  public static Optional<DepanFxWorkspaceResource> getBuiltinNodeKeyColumnResource(
      DepanFxWorkspace workspace) {
    return getBuiltinResource(
        workspace, DepanFxNodeKeyColumnData.class, NODE_KEY_COLUMN_TOOL_PATH);
  }

  private static Optional<DepanFxWorkspaceResource> getBuiltinResource(
      DepanFxWorkspace workspace, Class<?> type, Path toolPath) {
    return DepanFxProjects.getBuiltIn(
        workspace, type, c -> c.getPath().equals(toolPath));
  }
}
