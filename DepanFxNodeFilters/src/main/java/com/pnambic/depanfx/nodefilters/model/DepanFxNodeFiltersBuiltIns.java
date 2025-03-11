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
package com.pnambic.depanfx.nodefilters.model;

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeKindFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.FilterMergeMode;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class DepanFxNodeFiltersBuiltIns {

  public static final String ALL_NODES_FILTER_LABEL = "All Nodes Filter";

  public static final String ALL_NODES_FILTER_DESCR = "All nodes filter.";

  public static final String All_NODES_FILTER_DOC_NAME = "All Nodes";

  public static final Path ALL_NODES_FILTER_DOC_PATH =
      DepanFxBaseFilterData.NODE_FILTERS_TOOL_PATH
          .resolve(All_NODES_FILTER_DOC_NAME);

  private final DepanFxNodeKindFilterData allNodesFilterDoc =
      buildAllEdgeMatcher();

  public DepanFxNodeFiltersBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxBaseToolData>
      allNodeMatcher() {

    return new DepanFxBuiltInContribution.Simple<>(
        ALL_NODES_FILTER_DOC_PATH, allNodesFilterDoc);
  }

  private DepanFxNodeKindFilterData buildAllEdgeMatcher() {
    return new DepanFxNodeKindFilterData(
        ALL_NODES_FILTER_LABEL, ALL_NODES_FILTER_DESCR,
        FilterMergeMode.REPLACE, null, false);
  }
}
