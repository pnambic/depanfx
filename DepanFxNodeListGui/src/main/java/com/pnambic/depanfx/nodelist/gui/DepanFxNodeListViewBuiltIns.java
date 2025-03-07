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
package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumnBuiltIns;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionBuiltIns;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DepanFxNodeListViewBuiltIns {

  public static final Path FLAT_TABLE_VIEW_PATH =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
          .resolve("Flat Table View");

  public static final Path MEMBER_TABLE_VIEW_PATH =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
          .resolve("Member Table View");

  @Autowired
  public DepanFxNodeListViewBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      flatTableView() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData>(
        FLAT_TABLE_VIEW_PATH) {

      @Override
      protected DepanFxNodeListTableViewData buildDocument(
          DepanFxBuiltInProject project) {

        // Flat Section
        List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
            sectionRsrcs = new ArrayList<>();
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionBuiltIns.SIMPLE_SECTION_TOOL_PATH));

        // Node Kind Column
        List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
            columnRsrcs = new ArrayList<>();
        columnRsrcs.add(getResource(project,
            DepanFxNodeKeyColumnBuiltIns.KIND_KEY_COLUMN_TOOL_PATH));

        return new DepanFxNodeListTableViewData(
            "Flat Table View", "Flat node list table view",
            sectionRsrcs, columnRsrcs);
      }
    };
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      memberTableView() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData>(
        MEMBER_TABLE_VIEW_PATH) {

      @Override
      protected DepanFxNodeListTableViewData buildDocument(
          DepanFxBuiltInProject project) {

        // Flat and Members Sections
        List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
            sectionRsrcs = new ArrayList<>();
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionBuiltIns.MEMBER_TREE_SECTION_PATH));
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionBuiltIns.SIMPLE_SECTION_TOOL_PATH));

        // Node Kind Column
        List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
            columnRsrcs = new ArrayList<>();
        columnRsrcs.add(getResource(project,
            DepanFxNodeKeyColumnBuiltIns.KIND_KEY_COLUMN_TOOL_PATH));

        return new DepanFxNodeListTableViewData(
            "Member Table View", "Table view membership",
            sectionRsrcs, columnRsrcs);
      }
    };
  }
}
