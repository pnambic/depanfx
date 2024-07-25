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

import com.google.common.collect.ImmutableSet;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Evaluate a filter composed of a sequence of constituent filters.
 *
 * After each constituent filter is complete, include any closure or merge
 * behaviors, the result is provided as an input to the next sequential
 * filter.  This continues until every constituent filter is evaluated.
 *
 * The result of the final constituent filter is provided as the result of
 * {@link #computeResult(Collection)}.  This result is further subject to
 * the closure and merge behaviors provided by {@link DepanFxBaseFilter}.
 */
public class DepanFxSequenceFilter
    extends DepanFxBaseFilter<DepanFxSequenceFilterData>
    implements DepanFxClosableFilter {

  private final List<DepanFxBaseFilter<?>> filters;

  public DepanFxSequenceFilter(
      DepanFxSequenceFilterData filterData,
      DepanFxNodeFiltersRegistry nodeFiltersRegistry,
      GraphModel graphModel,
      Collection<GraphNode> filterNodes) {
    super(filterData);
    filters = buildFilters(nodeFiltersRegistry, graphModel, filterNodes);
  }

  @Override // DepanFxClosableFilter
  public boolean useClosure() {
    return getFilterData().useClosure();
  }

  public Stream<DepanFxBaseFilter<?>> streamFilters() {
    return filters.stream();
  }

  @Override // DepanFxBaseFilter
  protected Collection<GraphNode> computeResult(Collection<GraphNode> nodes) {
    Collection<GraphNode> result = ImmutableSet.copyOf(nodes);
    for (DepanFxBaseFilter<?> filter : filters) {
      result  = filter.computeNodes(result);
    }
    return result;
  }

  private List<DepanFxBaseFilter<?>> buildFilters(
      DepanFxNodeFiltersRegistry nodeFiltersRegistry,
      GraphModel graphModel, Collection<GraphNode> filterNodes) {
    NodeFilterFactory factory = new NodeFilterFactory(
            nodeFiltersRegistry, graphModel, filterNodes);
    return getFilterData().streamFilters()
        .map(factory::buildFilter)
        .collect(Collectors.toList());
  }

  public static class NodeFilterFactory {

    private final DepanFxNodeFiltersRegistry nodeFiltersRegistry;

    private final GraphModel graphModel;

    private final Collection<GraphNode> targetNodes;

    public NodeFilterFactory(
        DepanFxNodeFiltersRegistry nodeFiltersRegistry,
        GraphModel graphModel,
        Collection<GraphNode> targetNodes) {
      this.nodeFiltersRegistry = nodeFiltersRegistry;
      this.graphModel = graphModel;
      this.targetNodes = targetNodes;
    }

    public DepanFxBaseFilter<?> buildFilter(DepanFxBaseFilterData filterData) {
      return nodeFiltersRegistry.buildFilter(filterData, graphModel, targetNodes);
    }
  }
}
