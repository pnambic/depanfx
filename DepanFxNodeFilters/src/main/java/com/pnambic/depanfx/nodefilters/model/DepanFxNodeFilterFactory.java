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
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxMatcherFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxListFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxReferencedFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class DepanFxNodeFilterFactory {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFilterFactory.class);

  /**
   * For one-offs, where all the data is available.
   */
  public static DepanFxBaseFilter<?> buildFilter(
      DepanFxBaseFilterData filterData,
      GraphModel graphModel,
      Collection<GraphNode> filterNodes) {

    switch (filterData) {

    case DepanFxListFilterData listData:
      return new DepanFxListFilter(listData);

    case DepanFxMatcherFilterData matchData:
      return new DepanFxMatcherFilter(matchData, graphModel, filterNodes);

    case DepanFxReferencedFilterData refData:
      return new DepanFxReferencedFilter(refData, graphModel, filterNodes);

    case DepanFxSequenceFilterData seqData:
      return new DepanFxSequenceFilter(seqData, graphModel, filterNodes);

    default:
    }
    LOG.warn("Bad data type {} for filter", filterData.getClass().getName());
    throw new IllegalArgumentException("Unexpected value: " + filterData);
  }

  private final GraphModel graphModel;

  private final Collection<GraphNode> filterNodes;

  /**
   * For multiple filters from a source of filter data.
   */
  public DepanFxNodeFilterFactory(
      GraphModel graphModel,
      Collection<GraphNode> filterNodes) {
    this.graphModel = graphModel;
    this.filterNodes = filterNodes;
  }

  public DepanFxBaseFilter<?> buildFilter(DepanFxBaseFilterData filterData) {
    return buildFilter(filterData, graphModel, filterNodes);
  }
}
