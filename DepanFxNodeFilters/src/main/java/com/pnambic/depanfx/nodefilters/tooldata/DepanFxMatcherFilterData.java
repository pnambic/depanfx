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
package com.pnambic.depanfx.nodefilters.tooldata;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

public class DepanFxMatcherFilterData extends DepanFxBaseFilterData {

  public static final String MATCHER_FILTER_TOOL_EXT = "dmfti";

  private final DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc;

  private final boolean matcherInverse;

  private final boolean matcherClosure;

  public DepanFxMatcherFilterData(
      String toolName, String toolDescription,
      FilterMergeMode mergeMode,
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc,
      boolean matcherInverse,
      boolean matcherClosure) {
    super(toolName, toolDescription, mergeMode);
    this.matcherRsrc = matcherRsrc;
    this.matcherInverse = matcherInverse;
    this.matcherClosure = matcherClosure;
  }

  public static DepanFxMatcherFilterData createMatcherFilterData(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    DepanFxLinkMatcherDocument matcherInfo = matcherRsrc.getResource();
    return new DepanFxMatcherFilterData(
        matcherInfo.getToolName() + " filter",
        "Filter for " + matcherInfo.getToolName(),
        FilterMergeMode.REPLACE, matcherRsrc, false, false);
  }

  public DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> getMatcherResource() {
    return matcherRsrc;
  }

  public boolean useInverse() {
    return matcherInverse;
  }

  public boolean useClosure() {
    return matcherClosure;
  }
}
