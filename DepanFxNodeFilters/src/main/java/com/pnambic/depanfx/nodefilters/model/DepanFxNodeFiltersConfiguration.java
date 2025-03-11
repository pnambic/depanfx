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
import com.pnambic.depanfx.nodefilters.persistence.ListFilterPersistenceContribution;
import com.pnambic.depanfx.nodefilters.persistence.MatcherFilterPersistenceContribution;
import com.pnambic.depanfx.nodefilters.persistence.NodeKindFilterPersistenceContribution;
import com.pnambic.depanfx.nodefilters.persistence.ReferencedFilterPersistenceContribution;
import com.pnambic.depanfx.nodefilters.persistence.SequenceFilterPersistenceContribution;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxListFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxMatcherFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeKindFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxReferencedFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
public class DepanFxNodeFiltersConfiguration {

  @Bean
  public DepanFxNodeFiltersRegistry.Contribution listNodeFilter() {
    return new NodeFilterContrib(DepanFxListFilterData.class) {

      @Override
      public DepanFxBaseFilter<?> buildFilter(
          DepanFxBaseFilterData filterData,
          DepanFxNodeFiltersRegistry nodeFilterRegistry,
          GraphModel graphModel,
          Collection<GraphNode> targetNodes) {
        return new DepanFxListFilter((DepanFxListFilterData) filterData);
      }

      @Override
      public void prepareTransport(PersistDocumentTransportBuilder builder) {
        prepareTransport(builder,
            ListFilterPersistenceContribution.LIST_FILTER_INFO_TAG,
            DepanFxListFilterData.class);
      }
    };
  }

  @Bean
  public DepanFxNodeFiltersRegistry.Contribution matcherNodeFilter() {
    return new NodeFilterContrib(DepanFxMatcherFilterData.class) {

      @Override
      public DepanFxBaseFilter<?> buildFilter(
          DepanFxBaseFilterData filterData,
          DepanFxNodeFiltersRegistry nodeFilterRegistry,
          GraphModel graphModel,
          Collection<GraphNode> targetNodes) {
        return new DepanFxMatcherFilter(
            (DepanFxMatcherFilterData) filterData, graphModel, targetNodes);
      }

      @Override
      public void prepareTransport(PersistDocumentTransportBuilder builder) {
        prepareTransport(builder,
            MatcherFilterPersistenceContribution.MATCHER_FILTER_INFO_TAG,
            DepanFxMatcherFilterData.class);
      }
    };
  }

  @Bean
  public DepanFxNodeFiltersRegistry.Contribution nodeKindNodeFilter() {
    return new NodeFilterContrib(DepanFxNodeKindFilterData.class) {

      @Override
      public DepanFxBaseFilter<?> buildFilter(
          DepanFxBaseFilterData filterData,
          DepanFxNodeFiltersRegistry nodeFilterRegistry,
          GraphModel graphModel,
          Collection<GraphNode> targetNodes) {
        return new DepanFxNodeKindFilter(
            (DepanFxNodeKindFilterData) filterData, graphModel, targetNodes);
      }

      @Override
      public void prepareTransport(PersistDocumentTransportBuilder builder) {
        prepareTransport(builder,
            NodeKindFilterPersistenceContribution.NODE_KIND_FILTER_INFO_TAG,
            DepanFxNodeKindFilterData.class);
      }
    };
  }

  @Bean
  public DepanFxNodeFiltersRegistry.Contribution referencedNodeFilter() {
    return new NodeFilterContrib(DepanFxReferencedFilterData.class) {

      @Override
      public DepanFxBaseFilter<?> buildFilter(
          DepanFxBaseFilterData filterData,
          DepanFxNodeFiltersRegistry nodeFilterRegistry,
          GraphModel graphModel,
          Collection<GraphNode> targetNodes) {
        return new DepanFxReferencedFilter(
            (DepanFxReferencedFilterData) filterData,
            nodeFilterRegistry, graphModel, targetNodes);
      }

      @Override
      public void prepareTransport(PersistDocumentTransportBuilder builder) {
        prepareTransport(builder,
            ReferencedFilterPersistenceContribution.REFERENCED_FILTER_INFO_TAG,
            DepanFxReferencedFilterData.class);
      }
    };
  }

  @Bean
  public DepanFxNodeFiltersRegistry.Contribution sequenceNodeFilter() {
    return new NodeFilterContrib(DepanFxSequenceFilterData.class) {

      @Override
      public DepanFxBaseFilter<?> buildFilter(
          DepanFxBaseFilterData filterData,
          DepanFxNodeFiltersRegistry nodeFilterRegistry,
          GraphModel graphModel,
          Collection<GraphNode> targetNodes) {
        return new DepanFxSequenceFilter(
            (DepanFxSequenceFilterData) filterData,
            nodeFilterRegistry, graphModel, targetNodes);
      }

      @Override
      public void prepareTransport(PersistDocumentTransportBuilder builder) {
        prepareTransport(builder,
            SequenceFilterPersistenceContribution.SEQUENCE_FILTER_INFO_TAG,
            DepanFxSequenceFilterData.class);
      }
    };
  }

  public static abstract class NodeFilterContrib
      implements DepanFxNodeFiltersRegistry.Contribution {

    private final Class<?> contribType;

    private NodeFilterContrib(Class<?> contribType) {
      this.contribType = contribType;
    }

    @Override
    public boolean accepts(DepanFxBaseFilterData filter) {
      return contribType.isAssignableFrom(filter.getClass());
    }

    protected void prepareTransport(PersistDocumentTransportBuilder builder,
        String aliasTag, Class<?> transportType) {
      Class<?>[] allowType = new Class<?>[] { transportType };
      builder.addAllowedType(allowType);
      builder.addAlias(aliasTag, transportType);
    }
  }
}
