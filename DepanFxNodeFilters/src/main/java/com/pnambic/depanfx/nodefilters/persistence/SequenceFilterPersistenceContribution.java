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
package com.pnambic.depanfx.nodefilters.persistence;

import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.FilterMergeMode;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SequenceFilterPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxSequenceFilterData.SEQUENCE_FILTER_TOOL_EXT;

  public static final String SEQUENCE_FILTER_INFO_TAG =
      "sequence-filter-info";

  public static final String MERGE_MODE_TAG = "merge-mode";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxSequenceFilterData.class,
      FilterMergeMode.class,
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  private final DepanFxNodeFiltersRegistry nodeFiltersRegistry;

  @Autowired
  public SequenceFilterPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry,
      DepanFxNodeFiltersRegistry nodeFiltersRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
    this.nodeFiltersRegistry = nodeFiltersRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxSequenceFilterData.class.isAssignableFrom(
        document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    nodeFiltersRegistry.prepareTransport(builder);
    builder.addAlias(MERGE_MODE_TAG, FilterMergeMode.class);

    builder.addAllowedType(ALLOW_TYPES);

    // For the embedded filter.
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
