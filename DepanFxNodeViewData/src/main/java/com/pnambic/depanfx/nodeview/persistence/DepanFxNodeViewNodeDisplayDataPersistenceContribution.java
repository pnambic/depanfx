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
package com.pnambic.depanfx.nodeview.persistence;

import com.pnambic.depanfx.graph_doc.persistence.ContextModelIdConverter;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepanFxNodeViewNodeDisplayDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxNodeViewNodeDisplayData.NODE_VIEW_NODE_DISPLAY_EXT;

  private static final String NODE_DISPLAY_INFO_TAG = "node-display-info";

  private static final String NODE_DISPLAY_ENTRY_TAG = "node-display";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxNodeViewNodeDisplayData.class,
      DepanFxNodeViewNodeDisplayData.NodeDisplayEntry.class
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @Autowired
  public DepanFxNodeViewNodeDisplayDataPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxNodeViewNodeDisplayData.class
        .isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    ContextModelIdConverter.installIn(builder);

    builder.addAlias(
        NODE_DISPLAY_INFO_TAG, DepanFxNodeViewNodeDisplayData.class);
    builder.addImplicitCollection(
        DepanFxNodeViewNodeDisplayData.class, "nodeDisplayEntries");

    builder.addAlias(
        NODE_DISPLAY_ENTRY_TAG,
        DepanFxNodeViewNodeDisplayData.NodeDisplayEntry.class);

    builder.addAllowedType(ALLOW_TYPES);

    // Apply plugins for document elements.
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
