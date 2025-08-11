/*
 * Copyright 2025 The Depan Project Authors
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
package com.pnambic.depanfx.nodelist.persistence;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListEdgeMatcherData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.stereotype.Component;

@Component
public class NodeListEdgeMatcherPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxNodeListEdgeMatcherData.NODE_LIST_EDGE_MATCHER_TOOL_EXT;

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxNodeListEdgeMatcherData.class
  };

  private static final String NODE_LIST_EDGE_MATCHER_INFO_TAG =
      "node-list-edge-matcher";

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  public NodeListEdgeMatcherPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxNodeListEdgeMatcherData.class
        .isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAlias(
        NODE_LIST_EDGE_MATCHER_INFO_TAG, DepanFxNodeListEdgeMatcherData.class);

    builder.addAllowedType(ALLOW_TYPES);

    // Apply plugins for document elements.
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
