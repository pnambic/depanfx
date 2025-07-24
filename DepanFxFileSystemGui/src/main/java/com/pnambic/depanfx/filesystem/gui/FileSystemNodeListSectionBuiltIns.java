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
import com.pnambic.depanfx.filesystem.nodelist.link.FileSystemLinkMatcherBuiltIns;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxContainerOrder;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class FileSystemNodeListSectionBuiltIns {

  public static final Path FILE_SYSTEM_SECTION_PATH =
      DepanFxNodeListSectionData.SECTIONS_TOOL_PATH
          .resolve(FileSystemContextModelId.FILE_SYSTEM_KEY);

  public static final String FILE_SYSTEM_HIERARCHY_SECTION_NAME =
      "File System Hierarchy";

  public static final Path FILE_SYSTEM_HIERARCHY_SECTION_PATH =
      FILE_SYSTEM_SECTION_PATH.resolve(FILE_SYSTEM_HIERARCHY_SECTION_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeSectionData>
      fileSystemHierarchySection() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxTreeSectionData>(
        FILE_SYSTEM_HIERARCHY_SECTION_PATH) {

      @Override
      protected DepanFxTreeSectionData buildDocument(
          DepanFxBuiltInProject project) {

        return new DepanFxTreeSectionData(
            "File System Hierarchy Section",
            "Tree section based on File System  member relations.",
            "Hierarchy",
            true,
            getResource(project,
                FileSystemLinkMatcherBuiltIns.FILE_SYSTEM_MEMBER_MATCHER_PATH),
            false,
            OrderBy.NODE_LEAF,
            DepanFxContainerOrder.LAST,
            OrderDirection.FORWARD);
      }
    };
  }
}
