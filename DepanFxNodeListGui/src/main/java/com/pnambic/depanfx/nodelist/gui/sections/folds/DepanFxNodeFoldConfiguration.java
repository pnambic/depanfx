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
package com.pnambic.depanfx.nodelist.gui.sections.folds;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFoldSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistryContribution;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepanFxNodeFoldConfiguration {

  @Bean
  public DepanFxResourceRegistryContribution<GraphDocument>
  newNodeFold() {
    return new NodeFoldResourceContribution();
  }

  @Bean
  public DocumentPersistenceContribution foldSectionDataPeristenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    return new FoldSectionDataPersistenceContribution(graphNodeRegistry);
  }

  /////////////////////////////////////
  // Implementation Classes

  private static class NodeFoldResourceContribution
      extends DepanFxResourceRegistryContribution.Additional<GraphDocument>
      implements DepanFxResourceRegistryContribution.Dialog<GraphDocument> {

    private static final String CREATE_NODE_FOLDING_LABEL =
        "Create Node Folding...";

    private static final String CREATE_NODE_FOLDING_LABEL_KEY =
        "Node Folding";

    public NodeFoldResourceContribution() {
      super(
          CREATE_NODE_FOLDING_LABEL,
          GraphDocument.class,
          GraphDocPersistenceContribution.EXTENSION,
          CREATE_NODE_FOLDING_LABEL_KEY);
    }

    @Override
    public void runDialog(DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<GraphDocument> foldRsrc) {
      DepanFxNodeFoldData foldInfo =
          DepanFxNodeFoldData.emptyNodeFoldData(foldRsrc);
      DepanFxNodeFoldToolDialog.runCreateDialog(
          workspace, dialogRunner, workspace.addScratchResource(foldInfo));
    }
  }

  private class FoldSectionDataPersistenceContribution
      implements DocumentPersistenceContribution {

    private static final Class<?>[] ALLOWED_TYPES = new Class<?>[] {
        DepanFxFoldSectionData.class
    };

    private final GraphNodePersistencePluginRegistry graphNodeRegistry;

    public FoldSectionDataPersistenceContribution(
        GraphNodePersistencePluginRegistry graphNodeRegistry) {
      this.graphNodeRegistry = graphNodeRegistry;
    }

    @Override
    public boolean acceptsDocument(Object document) {
      return DepanFxFoldSectionData.class.isAssignableFrom(
          document.getClass());
    }

    @Override
    public boolean acceptsExt(String extText) {
      return DepanFxFoldSectionData.FOLD_SECTION_TOOL_EXT
          .equalsIgnoreCase(extText);
    }

    @Override
    public void prepareTransport(PersistDocumentTransportBuilder builder) {
      builder.addAllowedType(ALLOWED_TYPES);
      builder.addAlias("fold-section-info", DepanFxFoldSectionData.class);

      graphNodeRegistry.applyExtensions(
          builder, DepanFxWorkspaceResource.class);
    }
  }
}
