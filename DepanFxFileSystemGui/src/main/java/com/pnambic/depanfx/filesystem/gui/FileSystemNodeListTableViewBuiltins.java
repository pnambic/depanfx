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
package com.pnambic.depanfx.filesystem.gui;

import com.pnambic.depanfx.filesystem.context.FileSystemContextModelId;
import com.pnambic.depanfx.nodelist.gui.columns.infos.DepanFxNodeKeyColumnBuiltIns;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionBuiltIns;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FileSystemNodeListTableViewBuiltins {

  public static final Path FILE_SYSTEM_TABLE_VIEW_PATH =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
          .resolve(FileSystemContextModelId.FILE_SYSTEM_KEY);

  public static final String FILE_SYSTEM_HIERARY_TABLE_VIEW_NAME =
      "File System Hierarchy";

  public static final Path FILE_SYSTEM_HIERARY_TABLE_VIEW_PATH =
      FILE_SYSTEM_TABLE_VIEW_PATH.resolve(FILE_SYSTEM_HIERARY_TABLE_VIEW_NAME);

  public static final Path FILE_SYSTEM_TABLE_VIEW_CONTEXT_RESOURCE_PATH =
      FILE_SYSTEM_TABLE_VIEW_PATH.resolve(
          DepanFxNodeListTableViewData.TABLE_VIEW_CONTEXT_RESOURCE_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      fileSystemHierarchyTableView() {

    return new TableViewBuiltin(FILE_SYSTEM_HIERARY_TABLE_VIEW_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      fileSystemAsMemberTableViewContext() {

    return new TableViewBuiltin(FILE_SYSTEM_TABLE_VIEW_CONTEXT_RESOURCE_PATH);
  }

  private final class TableViewBuiltin extends
      DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData> {
    private TableViewBuiltin(Path path) {
      super(path);
    }

    @Override
    protected DepanFxNodeListTableViewData buildDocument(
        DepanFxBuiltInProject project) {

      // Flat and Members Sections
      List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
          sectionRsrcs = new ArrayList<>();
      sectionRsrcs.add(getResource(project,
          FileSystemNodeListSectionBuiltIns.FILE_SYSTEM_HIERARCHY_SECTION_PATH));
      sectionRsrcs.add(getResource(project,
          DepanFxNodeListSectionBuiltIns.SIMPLE_SECTION_TOOL_PATH));

      // Node Kind Column
      List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
          columnRsrcs = new ArrayList<>();
      columnRsrcs.add(getResource(project,
          DepanFxNodeKeyColumnBuiltIns.KIND_KEY_COLUMN_TOOL_PATH));

      return new DepanFxNodeListTableViewData(
          "File System Hierarchy Table View",
          "Node list view by File System member relations",
          sectionRsrcs, columnRsrcs);
    }
  }
}
