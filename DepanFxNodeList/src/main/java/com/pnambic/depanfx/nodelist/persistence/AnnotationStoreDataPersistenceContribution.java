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

import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxAnnotationStoreData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.stereotype.Component;

@Component
public class AnnotationStoreDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxAnnotationStoreData.ANNOTATION_STORE_TOOL_EXT;

  public static final String ANNOTATION_STORE_INFO_TAG =
      "annotation-store-info";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxAnnotationStoreData.class
  };

  private final DepanFxInfoRegistry infoRegistry;

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  public AnnotationStoreDataPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry,
      DepanFxInfoRegistry infoRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
    this.infoRegistry = infoRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxAnnotationStoreData.class.isAssignableFrom(
        document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAllowedType(ALLOW_TYPES);
    builder.addAlias(ANNOTATION_STORE_INFO_TAG,
        DepanFxAnnotationStoreData.class);
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);

  }
}
