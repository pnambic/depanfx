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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeKindFilterData;

import java.util.Collection;
import java.util.stream.Collectors;

public class DepanFxNodeKindFilter
    extends DepanFxBaseFilter<DepanFxNodeKindFilterData> {

  public DepanFxNodeKindFilter(
      DepanFxNodeKindFilterData filterData,
      GraphModel graphModel,
      Collection<GraphNode> targets) {
    super(filterData);
  }

  @Override // DepanFxBaseFilter
  protected Collection<GraphNode> computeResult(Collection<GraphNode> nodes) {
    Predicate<GraphNode> nodePredicate = getFilterPredicate();
    return nodes.stream()
        .filter(nodePredicate)
        .collect(Collectors.toList());
  }

  private Predicate<GraphNode> getFilterPredicate() {
    if (getFilterData().getNodeKind() != null) {
      if (getFilterData().isExclusionFilter()) {
        return this::exclusionTest;
      }
      return this::inclusionTest;
    }
    if (getFilterData().isExclusionFilter()) {
      return Predicates.alwaysFalse();
    }
    return Predicates.alwaysTrue();
  }

  private boolean inclusionTest(GraphNode node) {
    return getFilterData().getNodeKind().equals(
        node.getId().getContextNodeKindId());
  }

  private boolean exclusionTest(GraphNode node) {
    return !inclusionTest(node);
  }
}
