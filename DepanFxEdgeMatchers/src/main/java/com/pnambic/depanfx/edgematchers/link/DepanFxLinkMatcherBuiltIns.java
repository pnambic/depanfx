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
package com.pnambic.depanfx.edgematchers.link;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.graph.context.BaseContextDefinition;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Collections;

@Configuration
public class DepanFxLinkMatcherBuiltIns {

  public static final Path MEMBER_MATCHER_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH.resolve("Tree Member");

  public static final String MATCH_ALL_MATCHER_LABEL = "All Edges";

  public static final String MATCH_ALL_MATCHER_DESCR = "All Edges";

  public static final String MATCH_ALL_DOC_NAME = "All Edges";

  public static final Path MATCH_ALL_DOC_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
          .resolve(MATCH_ALL_DOC_NAME);

  private final DepanFxLinkMatcherDocument allEdgeMatcherDoc =
      buildAllEdgeMatcher();

  public DepanFxLinkMatcherBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      memberFinderLinkMatcher() {

    DepanFxLinkMatcherDocument finderMatcher =
        new DepanFxLinkMatcherDocument(
            "Tree Member", "Synthetic tree membership",
            null, DepanFxLinkMatcherGroup.MEMBER_MATCHER_GROUP, null);
    return new DepanFxBuiltInContribution.Simple<>(
        MEMBER_MATCHER_PATH, finderMatcher);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      allEdgeMatcher() {

    return new DepanFxBuiltInContribution.Simple<>(
        MATCH_ALL_DOC_PATH, allEdgeMatcherDoc);
  }

  private DepanFxLinkMatcherDocument buildAllEdgeMatcher() {
    return new DepanFxLinkMatcherDocument(
          MATCH_ALL_MATCHER_LABEL,
          MATCH_ALL_MATCHER_DESCR,
          BaseContextDefinition.MODEL_ID,
          Collections.emptyList(),
          DepanFxLinkMatchers.ALL_EDGES_FORWARD);
  }
}
