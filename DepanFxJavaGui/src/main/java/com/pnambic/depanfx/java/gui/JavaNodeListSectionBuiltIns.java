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
import com.pnambic.depanfx.java.edgematchers.link.JavaLinkMatcherBuiltIns;
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
public class JavaNodeListSectionBuiltIns {

  public static final Path JAVA_SECTION_PATH =
      DepanFxNodeListSectionData.SECTIONS_TOOL_PATH
          .resolve(JavaContextModelId.JAVA_KEY);

  public static final String TREE_MEMBERSHIP_TREE_SECTION_NAME =
      "Tree Membership Tree Section";

  public static final Path TREE_MEMBERSHIP_TREE_SECTION_PATH =
      JAVA_SECTION_PATH.resolve(TREE_MEMBERSHIP_TREE_SECTION_NAME);

  public static final String DERIVED_CLASS_TREE_SECTION_NAME =
      "Derived Class Tree Section";

  public static final Path DERIVED_CLASS_TREE_SECTION_PATH =
      JAVA_SECTION_PATH.resolve(DERIVED_CLASS_TREE_SECTION_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeSectionData>
      javaTreeMemberTreeSection() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxTreeSectionData>(
        TREE_MEMBERSHIP_TREE_SECTION_PATH) {

      @Override
      protected DepanFxTreeSectionData buildDocument(
          DepanFxBuiltInProject project) {

        return new DepanFxTreeSectionData(
            "Java Tree Section",
            "Tree section based on Java tree member relations.",
            "Java Tree",
            true,
            getResource(project, JavaLinkMatcherBuiltIns.JAVA_TREE_MEMBER_MATCHER_PATH),
            false,
            OrderBy.NODE_LEAF,
            DepanFxContainerOrder.LAST,
            OrderDirection.FORWARD);
      }
    };
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeSectionData>
      javaDerivedClassTreeSection() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxTreeSectionData>(
        DERIVED_CLASS_TREE_SECTION_PATH) {

      @Override
      protected DepanFxTreeSectionData buildDocument(
          DepanFxBuiltInProject project) {

        return new DepanFxTreeSectionData(
            "Derived Class Section",
            "Tree section based on Java derived class relations.",
            "Derived Class Tree",
            true,
            getResource(project, JavaLinkMatcherBuiltIns.JAVA_CLASS_DERIVED_MATCHER_PATH),
            false,
            OrderBy.NODE_LEAF,
            DepanFxContainerOrder.LAST,
            OrderDirection.FORWARD);
      }
    };
  }
}
