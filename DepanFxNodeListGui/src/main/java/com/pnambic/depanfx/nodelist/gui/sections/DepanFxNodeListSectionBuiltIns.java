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
package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherBuiltIns;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxContainerOrder;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class DepanFxNodeListSectionBuiltIns {

  public static final String MEMBER_TREE_SECTION_NAME = "Member Tree";

  public static final Path MEMBER_TREE_SECTION_PATH =
      DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.resolve(MEMBER_TREE_SECTION_NAME);

  public static final String SIMPLE_SECTION_NAME = "Simple Section";

  public static final Path SIMPLE_SECTION_TOOL_PATH =
      DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.resolve(SIMPLE_SECTION_NAME);

  @Autowired
  public DepanFxNodeListSectionBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeSectionData>
      memberTreeSection() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxTreeSectionData>(
        MEMBER_TREE_SECTION_PATH) {

      @Override
      protected DepanFxTreeSectionData buildDocument(
          DepanFxBuiltInProject project) {

        return new DepanFxTreeSectionData(
            "Member Tree Section",
            "Tree section based on a link matcher for membership",
            "Tree",
            true,
            getResource(project, DepanFxLinkMatcherBuiltIns.MEMBER_MATCHER_PATH),
            true,
            OrderBy.NODE_LEAF,
            DepanFxContainerOrder.LAST,
            OrderDirection.FORWARD);
      }
    };
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxFlatSectionData> flatSection() {
    DepanFxFlatSectionData toolData =
        new DepanFxFlatSectionData(
            "Built-in Flat Section", "Built-in flat section.",
            DepanFxFlatSectionData.BASE_SECTION_LABEL, true,
            OrderBy.NODE_KEY, OrderDirection.FORWARD);
    return new DepanFxBuiltInContribution.Simple<>(
        SIMPLE_SECTION_TOOL_PATH, toolData);
  }
}
