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

import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxReferencedFilterData;

import java.util.Collection;

public class DepanFxReferencedFilter
    extends DepanFxBaseFilter<DepanFxReferencedFilterData>
    implements DepanFxClosableFilter {

  private final GraphModel graphModel;

  private final Collection<GraphNode> targets;

  public DepanFxReferencedFilter(
      DepanFxReferencedFilterData filterData,
      GraphModel graphModel,
      Collection<GraphNode> targets) {
    super(filterData);
    this.graphModel = graphModel;
    this.targets = targets;
  }

  @Override // DepanFxClosableFilter
  public boolean useClosure() {
    return getFilterData().useClosure();
  }

  @Override // DepanFxBaseFilter
  protected Collection<GraphNode> computeResult(Collection<GraphNode> nodes) {
    DepanFxBaseFilterData filterData = getFilterData().getFilter();
    DepanFxBaseFilter<?> filter =
        DepanFxNodeFilterFactory.buildFilter(filterData, graphModel, targets);
    return filter.computeResult(nodes);
  }
}
