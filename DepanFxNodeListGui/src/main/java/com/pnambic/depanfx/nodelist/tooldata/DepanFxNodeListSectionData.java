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
public class DepanFxNodeListSectionData {

  public static final String SECTIONS_TOOL_DIR = "Sections";

  public static final String SIMPLE_SECTION_NAME = "Simple Section";

  public static final Path SECTIONS_TOOL_PATH =
      DepanFxNodeListData.NODE_LIST_TOOL_PATH.resolve(SECTIONS_TOOL_DIR);

  public static final Path SIMPLE_SECTION_TOOL_PATH =
      SECTIONS_TOOL_PATH.resolve(SIMPLE_SECTION_NAME);

  public enum OrderBy { NODE_ID, NODE_KEY, NODE_LEAF };

  public enum OrderDirection { FORWARD, REVERSE };

  public static Optional<DepanFxWorkspaceResource> getBuiltinSimpleSectionResource(
      DepanFxWorkspace workspace) {
    return DepanFxProjects.getBuiltIn(
        workspace, DepanFxFlatSectionData.class, SIMPLE_SECTION_TOOL_PATH);
  }
}
