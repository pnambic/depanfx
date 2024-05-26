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

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxMatcherFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.FilterMergeMode;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatcherFilterPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxMatcherFilterData.MATCHER_FILTER_TOOL_EXT;

  public static final String MATCHER_FILTER_INFO_TAG = "matcher-filter-info";

  public static final String MERGE_MODE_TAG = "merge-mode";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxMatcherFilterData.class,
      FilterMergeMode.class
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @Autowired
  public MatcherFilterPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxMatcherFilterData.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAlias(MATCHER_FILTER_INFO_TAG, DepanFxMatcherFilterData.class);
    builder.addAlias(MERGE_MODE_TAG, FilterMergeMode.class);

    builder.addAllowedType(ALLOW_TYPES);
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
