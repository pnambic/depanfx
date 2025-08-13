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

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphModel;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxMatcherFilterData;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLink;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepanFxMatcherFilter
    extends DepanFxBaseFilter<DepanFxMatcherFilterData>
    implements DepanFxClosableFilter {

  private final GraphModel graphModel;

  private final Collection<GraphNode> targets;

  public DepanFxMatcherFilter(
      DepanFxMatcherFilterData filterData,
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
    DepanFxLinkMatcher matcher =
        getFilterData().getMatcherResource().getResource().getMatcher();
    if (getFilterData().useInverse()) {
      return computeToSource(matcher, nodes);
    }
    return computeToTarget(matcher, nodes);
  }

  private Collection<GraphNode> computeToTarget(
      DepanFxLinkMatcher matcher, Collection<GraphNode> nodes) {

    return streamMatchLinks(matcher)
        .filter(l -> nodes.contains(l.getSource()))
        .map(l -> l.getTarget())
        .filter(n -> targets.contains(n))
        .collect(Collectors.toSet());
  }

  private Collection<GraphNode> computeToSource(
      DepanFxLinkMatcher matcher, Collection<GraphNode> nodes) {

    return streamMatchLinks(matcher)
        .filter(l -> nodes.contains(l.getTarget()))
        .map(l -> l.getSource())
        .filter(targets::contains)
        .collect(Collectors.toSet());
  }

  private Stream<DepanFxLink> streamMatchLinks(DepanFxLinkMatcher matcher) {
    return graphModel.streamEdges()
        .flatMap(e -> matcher.match(e).stream());
  }
}
