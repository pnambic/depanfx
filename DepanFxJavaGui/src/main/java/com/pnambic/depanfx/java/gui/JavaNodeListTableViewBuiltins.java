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
package com.pnambic.depanfx.java.gui;

import com.pnambic.depanfx.java.context.JavaContextModelId;
import com.pnambic.depanfx.nodelist.builtins.DepanFxNodeKeyColumnBuiltIns;
import com.pnambic.depanfx.nodelist.builtins.DepanFxNodeListSectionBuiltIns;
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
public class JavaNodeListTableViewBuiltins {

  public static final Path JAVA_TABLE_VIEW_PATH =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
          .resolve(JavaContextModelId.JAVA_KEY);

  public static final String TREE_MEMBER_TABLE_VIEW_NAME =
      "Tree Member Node List View";

  public static final Path TREE_MEMBER_TABLE_VIEW_PATH =
      JAVA_TABLE_VIEW_PATH.resolve(TREE_MEMBER_TABLE_VIEW_NAME);

  public static final String DERIVED_CLASS_TABLE_VIEW_NAME =
      "Derived Class Node List View";

  public static final Path DERIVED_CLASS_TABLE_VIEW_PATH =
      JAVA_TABLE_VIEW_PATH.resolve(DERIVED_CLASS_TABLE_VIEW_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      treeMembershipTableView() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData>(
        TREE_MEMBER_TABLE_VIEW_PATH) {

      @Override
      protected DepanFxNodeListTableViewData buildDocument(
          DepanFxBuiltInProject project) {

        // Flat and Members Sections
        List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
            sectionRsrcs = new ArrayList<>();
        sectionRsrcs.add(getResource(project,
            JavaNodeListSectionBuiltIns.TREE_MEMBERSHIP_TREE_SECTION_PATH));
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionBuiltIns.SIMPLE_SECTION_TOOL_PATH));

        // Node Kind Column
        List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
            columnRsrcs = new ArrayList<>();
        columnRsrcs.add(getResource(project,
            DepanFxNodeKeyColumnBuiltIns.KIND_KEY_COLUMN_TOOL_PATH));

        return new DepanFxNodeListTableViewData(
            "Tree Hierarchy View",
            "Table view based on tree member heirarchy",
            sectionRsrcs, columnRsrcs);
      }
    };
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      derivedClassTableView() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData>(
        DERIVED_CLASS_TABLE_VIEW_PATH) {

      @Override
      protected DepanFxNodeListTableViewData buildDocument(
          DepanFxBuiltInProject project) {

        // Flat and Members Sections
        List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
            sectionRsrcs = new ArrayList<>();
        sectionRsrcs.add(getResource(project,
            JavaNodeListSectionBuiltIns.DERIVED_CLASS_TREE_SECTION_PATH));
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionBuiltIns.SIMPLE_SECTION_TOOL_PATH));

        // Node Kind Column
        List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
            columnRsrcs = new ArrayList<>();
        columnRsrcs.add(getResource(project,
            DepanFxNodeKeyColumnBuiltIns.KIND_KEY_COLUMN_TOOL_PATH));

        return new DepanFxNodeListTableViewData(
            "Class Hierarchy View", "Table view based on class derivation heirarchy",
            sectionRsrcs, columnRsrcs);
      }
    };
  }
}
