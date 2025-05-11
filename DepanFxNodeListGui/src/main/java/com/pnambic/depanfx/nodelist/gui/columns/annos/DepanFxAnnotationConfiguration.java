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
package com.pnambic.depanfx.nodelist.gui.columns.annos;

import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxAnnotationStoreData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxInfoStoreData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

import javafx.scene.control.MenuItem;

@Configuration
public class DepanFxAnnotationConfiguration {

  private static final String ANNOTATION_INDEX_TOOL_NAME =
      "Annotation Index";

  public static final String ANNOTATION_INDEX_TOOL_DESCR =
      "Index of annotation keys to their infos.";

  private static final String ANNOTATION_STORE_TOOL_NAME =
      "Node Info Store";

  public static final String ANNOTATION_STORE_TOOL_DESCR =
      "Store of info about nodes.";

  /////////////////////////////////////
  // Annotation Index Beans

  @Bean
  public DepanFxNewResourceContribution newAnnotationIndex(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    return new NewAnnotationIndexContribution(workspace, dialogRunner);
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  annotationIndexFileOpenContribution() {
    return new AnnotationIndexFileOpenContribution();
  }

  /////////////////////////////////////
  // Info Index Beans

  @Bean
  public DepanFxResourceRegistry.Contribution newAnnotationStore(
      DepanFxWorkspace workspace) {
    return new NodeInfoResourceContribution(workspace);
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  annotationStoreFileOpenContribution(DepanFxWorkspace workspace) {
    return new InfoStoreFileOpenContribution(workspace);
  }

  /////////////////////////////////////
  // Class definitions

  private class NewAnnotationIndexContribution
    implements DepanFxNewResourceContribution {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    public NewAnnotationIndexContribution(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
    }

    @Override
    public MenuItem createNewResourceMenuItem() {
      return DepanFxContextMenuBuilder.createActionItem(
          ANNOTATION_INDEX_TOOL_NAME, e -> runCreateDialog());
    }

    private void runCreateDialog() {
      DepanFxAnnotationIndexData annoData =
          new DepanFxAnnotationIndexData(
              ANNOTATION_INDEX_TOOL_NAME, ANNOTATION_INDEX_TOOL_DESCR,
              Collections.emptyList());
      DepanFxAnnotationIndexToolDialog.runCreateDialog(
          dialogRunner, workspace.addScratchResource(annoData));
    }
  }

  private static class AnnotationIndexFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxAnnotationIndexData> {

    public AnnotationIndexFileOpenContribution() {
      super(
          ANNOTATION_INDEX_TOOL_NAME,
          DepanFxAnnotationIndexData.class,
          DepanFxAnnotationIndexData.ANNOTATION_INDEX_TOOL_EXT,
          ANNOTATION_INDEX_TOOL_NAME);
    }

    @Override
    protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsrc) {
      DepanFxAnnotationIndexToolDialog.runCreateDialog(
          dialogRunner, annoIndexRsrc);
    }
  }

  private static class NodeInfoResourceContribution
      extends DepanFxResourceRegistry.Additional<GraphDocument> {

    private static final String CREATE_NODE_INFO_STORE_LABEL =
        "Create Node Info Store...";

    private static final String CREATE_NODE_INFO_STORE_LABEL_KEY =
        "Node Info Store";

    private final DepanFxWorkspace workspace;

    public NodeInfoResourceContribution(DepanFxWorkspace workspace) {
      super(
          CREATE_NODE_INFO_STORE_LABEL,
          GraphDocument.class,
          GraphDocPersistenceContribution.EXTENSION,
          CREATE_NODE_INFO_STORE_LABEL_KEY);
      this.workspace = workspace;
    }

    @Override
    protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<GraphDocument> wkspRsrc) {
      DepanFxAnnotationStoreData newStore =
          new DepanFxAnnotationStoreData(
              ANNOTATION_STORE_TOOL_NAME,
              ANNOTATION_STORE_TOOL_DESCR,
              wkspRsrc, null);
      DepanFxAnnotationStoreToolDialog.runCreateDialog(
          workspace, dialogRunner, workspace.addScratchResource(newStore));
    }
  }

  private static class InfoStoreFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxAnnotationStoreData> {

    private final DepanFxWorkspace workspace;

    public InfoStoreFileOpenContribution(DepanFxWorkspace workspace) {
      super(
          ANNOTATION_STORE_TOOL_NAME,
          DepanFxAnnotationStoreData.class,
          DepanFxInfoStoreData.INFO_STORE_TOOL_EXT,
          ANNOTATION_STORE_TOOL_NAME);
      this.workspace = workspace;
    }

    @Override
    protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxAnnotationStoreData> infoStoreRsrc) {
      DepanFxAnnotationStoreToolDialog.runCreateDialog(
            workspace, dialogRunner, infoStoreRsrc);
    }
  }
}
