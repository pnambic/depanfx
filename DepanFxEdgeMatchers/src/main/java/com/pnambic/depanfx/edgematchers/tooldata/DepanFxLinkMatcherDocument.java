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
package com.pnambic.depanfx.edgematchers.tooldata;
import com.pnambic.depanfx.graph.context.ContextModelId;

import java.util.List;

/**
 * Wrap a tool name and description around a link matcher.
 *
 * <p>Since link matchers on their own are rarely serializable,
 * this class is primarily useful for the definition of built-in
 * matchers that do not need to be serialized and can be referenced
 * as a resource.  For example, all of the built-in File System and Java
 * matchers are created this way.
 */
public class DepanFxLinkMatcherDocument extends DepanFxBaseMatcherDocument {

  public static final String LINK_MATCHER_TOOL_EXT = "dlmti";

  private final ContextModelId contextModelId;

  private final List<DepanFxLinkMatcher> matchGroups;

  private final DepanFxLinkMatcher matcher;

  public DepanFxLinkMatcherDocument(
      String toolName, String toolDescription,
      ContextModelId contextModelId, List<DepanFxLinkMatcher> matchGroups,
      DepanFxLinkMatcher matcher) {
    super(toolName, toolDescription);
    this.contextModelId = contextModelId;
    this.matchGroups = matchGroups;
    this.matcher = matcher;
  }

  public ContextModelId getModelId() {
    return contextModelId;
  }

  public List<DepanFxLinkMatcher> getMatchGroups() {
    return matchGroups;
  }

  public DepanFxLinkMatcher getMatcher() {
    return matcher;
  }
}
