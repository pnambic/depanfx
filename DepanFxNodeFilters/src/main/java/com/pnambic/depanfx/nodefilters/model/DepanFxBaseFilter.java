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
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.FilterMergeMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base implementation of node filters that provides closure for a node
 * relationship and a complete set of source merge options.
 */
public abstract class DepanFxBaseFilter<T extends DepanFxBaseFilterData> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxBaseFilter.class);

  /**
   * Carries the merge mode, which {@link #computeNodes(Collection)} uses
   * to compute the filter's result.
   */
  private final T filterData;

  protected DepanFxBaseFilter(T filterData) {
    this.filterData = filterData;
  }

  /**
   * Combine two collection of nodes in various ways.
   *
   * @param mergeMode - how to merge the collections
   * @param sourceNodes - list of nodes, not modifiable.
   * @param resultNodes - list of nodes, may be modified.
   * @return result of merge
   */
  public static Collection<GraphNode> mergeNodes(
      FilterMergeMode mergeMode,
      Collection<GraphNode> sourceNodes,
      Collection<GraphNode> resultNodes) {
    switch (mergeMode) {
    case A_SUB_B:
      return sourceNodes.stream()
          .filter(n -> !resultNodes.contains(n))
          .collect(Collectors.toSet());
    case B_SUB_A:
      resultNodes.removeAll(sourceNodes);
      return resultNodes;
    case INTERSECT:
      return intersect(sourceNodes, resultNodes);
    case KEEP:
      return sourceNodes;
    case REPLACE:
      return resultNodes;
    case UNION:
      return union(sourceNodes, resultNodes);
    }
    LOG.warn("Unrecognized merge mode", mergeMode);
    return Collections.emptyList();
  }

  public Collection<GraphNode> computeNodes(Collection<GraphNode> nodes) {
    Collection<GraphNode> result = computeResult(nodes);
    if (this instanceof DepanFxClosableFilter asClosure
        && asClosure.useClosure()) {
      result = computeClosure(result);
    }
    return mergeNodes(filterData.getMergeMode(), nodes, result);
  }

  public T getFilterData() {
    return filterData;
  }

  /**
   * @param nodes source collection of graph nodes is assumed to be immutable.
   *         It may be iterated or streamed, but it should not be changed.
   * @return this collection of graph nodes is assumed to be mutable,
   *         and may be changed during closure evaluation or source blending.
   */
  protected abstract Collection<GraphNode> computeResult(
      Collection<GraphNode> nodes);

  /**
   * @param nodes may be modified during closure evaluation.
   */
  private Collection<GraphNode> computeClosure(Collection<GraphNode> nodes) {

    Collection<GraphNode> result = new HashSet<>(nodes);
    Collection<GraphNode> update = new HashSet<>(result);

    while (!update.isEmpty()) {
      update = computeResult(update);
      update.removeAll(result);
      result.addAll(update);
    }
    return result;
  }

  private static Collection<GraphNode> intersect(
      Collection<GraphNode> oneNodes, Collection<GraphNode> twoNodes){
    if (oneNodes.size() < twoNodes.size()) {
      return intersectByLength(oneNodes, twoNodes);
    }
    return intersectByLength(twoNodes, oneNodes);
  }

  private static Collection<GraphNode> union(
      Collection<GraphNode> oneNodes, Collection<GraphNode> twoNodes){
    if (twoNodes instanceof Set<GraphNode>) {
      twoNodes.addAll(oneNodes);
      return twoNodes;
    }

    Set<GraphNode> result = new HashSet<>(oneNodes);
    result.addAll(twoNodes);
    return result;
  }

  private static Collection<GraphNode> intersectByLength(
      Collection<GraphNode> shortNodes, Collection<GraphNode> longNodes) {
    return shortNodes.stream()
        .filter(n -> !longNodes.contains(n))
        .collect(Collectors.toSet());
  }
}
