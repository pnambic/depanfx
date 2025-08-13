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
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Encapsulate common behaviors for node filters.
 */
@Component
public class DepanFxNodeFiltersRegistry {

  public interface NodeFilterFactory {
    DepanFxBaseFilter<?> buildFilter(DepanFxBaseFilterData filterData);
  }

  public interface Contribution {

    boolean accepts(DepanFxBaseFilterData filter);

    DepanFxBaseFilter<?> buildFilter(
        DepanFxBaseFilterData filter,
        DepanFxNodeFiltersRegistry nodeFilterRegistry,
        GraphModel graphModel,
        Collection<GraphNode> targetNodes);

    void prepareTransport(PersistDocumentTransportBuilder builder);
  }

  public static abstract class TransportableContribution implements Contribution {

    private final Class<?> contribType;

    public TransportableContribution(Class<?> contribType) {
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

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFiltersRegistry.class);

  private final List<Contribution> contribs;

  public DepanFxNodeFiltersRegistry(List<Contribution> contribs) {
    this.contribs = contribs;
  }

  public Optional<Boolean> getClosure(DepanFxBaseFilterData filter) {
    return DepanFxClosableFilter.getClosure(filter);
  }

  public DepanFxBaseFilter<?> buildFilter(
      DepanFxBaseFilterData filter,
      GraphModel graphModel,
      Collection<GraphNode> targetNodes) {
    return lookupContrib(filter, "buildFilter")
        .map(c -> c.buildFilter(filter, this, graphModel, targetNodes))
        .get();
  }

  public NodeFilterFactory buildFilterFactory(
      GraphModel graphModel, Collection<GraphNode> targetNodes) {
    return new RegistryFactory(graphModel, targetNodes);
  }

  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    contribs.forEach(c -> c.prepareTransport(builder));
  }

  private Optional<Contribution> lookupContrib(
      DepanFxBaseFilterData filter, String caller) {
    Optional<Contribution> result = contribs.stream()
        .filter(c -> c.accepts(filter))
        .findFirst();
    if (result.isEmpty()) {
      LOG.warn("Unexpected filter {} for {}",
          filter.getClass().getName(), caller);
    }
    return result;
  }

  private class RegistryFactory implements NodeFilterFactory {

    private final GraphModel graphModel;

    private final Collection<GraphNode> targetNodes;

    public RegistryFactory(
        GraphModel graphModel,
        Collection<GraphNode> targetNodes) {
      this.graphModel = graphModel;
      this.targetNodes = targetNodes;
    }

    @Override
    public DepanFxBaseFilter<?> buildFilter(DepanFxBaseFilterData filterData) {
      return DepanFxNodeFiltersRegistry.this.buildFilter(
          filterData, graphModel, targetNodes);
    }
  }
}
