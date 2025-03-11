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
package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DepanFxLinkMatcherSequenceDocument extends DepanFxBaseToolData {

  public static final String LINK_MATCHER_SEQUENCE_TOOL_EXT = "dlmsti";

  public static final String EDGE_VISIBILITY_CONTEXT_RESOURCE_NAME = "Edge Visibility";

  // Share persistence location with stand-alone link matchers.
  public static final String LINK_MATCHER_SEQUENCE_TOOL_DIR =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_DIR;

  public static final Path LINK_MATCHER_SEQUENCE_TOOL_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH;

  private final ContextModelId contextModelId;

  private final List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> matcherRefs;

  public DepanFxLinkMatcherSequenceDocument(
      String toolName, String toolDescription,
      ContextModelId contextModelId,
      List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> matcherRefs) {
    super(toolName, toolDescription);
    this.contextModelId = contextModelId;
    this.matcherRefs = matcherRefs;
  }

  public ContextModelId getModelId() {
    return contextModelId;
  }

  public Stream<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      streamMatchers() {
    return matcherRefs.stream();
  }

  public Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      getMatcher(GraphEdge edge) {
    return streamMatchers()
        .filter(m -> m.getResource().getMatcher().match(edge).isPresent())
        .findFirst();
  }

}
