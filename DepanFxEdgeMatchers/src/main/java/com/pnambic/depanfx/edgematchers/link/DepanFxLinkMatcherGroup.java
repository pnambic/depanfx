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
package com.pnambic.depanfx.edgematchers.link;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLink;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Uses instances of a matcher group only as a key for matcher lookup.
 */
public class DepanFxLinkMatcherGroup implements DepanFxLinkMatcher {

  @Override
  public Optional<DepanFxLink> match(GraphEdge edge) {
    return Optional.empty();
  }

  public static final DepanFxLinkMatcherGroup MEMBER =
      new DepanFxLinkMatcherGroup();

  public static final DepanFxLinkMatcherGroup USES =
      new DepanFxLinkMatcherGroup();

  public static final DepanFxLinkMatcherGroup REFS =
      new DepanFxLinkMatcherGroup();

  /////////////////////////////////////
  // Common composites

  public static final List<DepanFxLinkMatcher> MEMBER_MATCHER_GROUP =
      Arrays.asList(new DepanFxLinkMatcher[] { DepanFxLinkMatcherGroup.MEMBER });

  public static Optional<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
      getMemberMatcherRsrc(DepanFxWorkspace workspace, ContextModelId modelId) {
    return DepanFxProjects.getBuiltIn(
        workspace, DepanFxBaseMatcherDocument.class,
        c -> isContextModelMemberMatcher(modelId, c.getDocument()));
  }

  /**
   * For referenced matchers, a null model id indicates that
   * the owner tool will resolve the actual member matchers later on.
   */
  public static boolean isContextModelMatcherResource(
      ContextModelId modelId,
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc) {
    DepanFxBaseMatcherDocument matcher = matcherRsrc.getResource();
    if (matcher instanceof DepanFxLinkMatcherDocument linkInfo) {
      if (!linkInfo.getMatchGroups()
          .contains(DepanFxLinkMatcherGroup.MEMBER)) {
        return false;
      }

      // [29-Nov-2023] Kludge for matches any, actual matcher provided later.
      if (linkInfo.getModelId() == null) {
        return true;
      }
      return linkInfo.getModelId().equals(modelId);
    }
    return false;
  }

  private static boolean isContextModelMemberMatcher(
      ContextModelId modelId, DepanFxBaseMatcherDocument linkMatchDoc) {
    if (linkMatchDoc instanceof DepanFxLinkMatcherDocument linkInfo) {
      if (!linkInfo.getMatchGroups()
          .contains(DepanFxLinkMatcherGroup.MEMBER)) {
        return false;
      }
      // Avoid NPE, not what we are looking for.
      if (linkInfo.getModelId() == null) {
        return false;
      }
      return linkInfo.getModelId().equals(modelId);
    }

    return false;
  }
}
