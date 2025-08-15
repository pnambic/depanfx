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
package com.pnambic.depanfx.edgematchers.tooldata;

import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class DepanFxLinkMatcherSequenceDocument
    extends DepanFxBaseMatcherDocument {

  public static final String LINK_MATCHER_SEQUENCE_TOOL_EXT = "dlmsti";

  // Share persistence location with stand-alone link matchers.
  public static final String LINK_MATCHER_SEQUENCE_TOOL_DIR =
      LINK_MATCHER_TOOL_DIR;

  public static final Path LINK_MATCHER_SEQUENCE_TOOL_PATH =
      LINK_MATCHER_TOOL_PATH;

  private final List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> matcherRefs;

  public DepanFxLinkMatcherSequenceDocument(
      String toolName, String toolDescription,
      List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> matcherRefs) {
    super(toolName, toolDescription);
    this.matcherRefs = matcherRefs;
  }

  public Stream<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
      streamMatchers() {
    return matcherRefs.stream();
  }
}
