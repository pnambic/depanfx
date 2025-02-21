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
package com.pnambic.depanfx.nodelist.viewer;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodelist.builtins.DepanFxNodeListViewBuiltIns;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class DepanFxNodeListViewerConfiguration {

  public static final String OPEN_AS_LIST_LABEL = "Open as Node List";

  public static final String OPEN_AS_LIST_ORDER_KEY = "Node List";

  public static final String OPEN_GRAPH_AS_LIST_LABEL = "Open as Node List";

  public static final String OPEN_GRAPH_AS_LIST_ORDER_KEY = "Graph Document";

  @Autowired
  public DepanFxNodeListViewerConfiguration() {
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  nodeListAsListResourceContribution() {
    return new NodeListResourceContribution();
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  graphDocAsListResourceContribution() {
    return new GraphDocResourceContribution();
  }

  @Bean
  public DepanFxSceneMenuContribution nodeListEditSelectAll() {

    return new DepanFxSceneMenuContribution.Action<DepanFxNodeListViewer>(
        DepanFxSceneMenuItems.SELECTION_ALL_ITEM,
        DepanFxNodeListViewer.class,
        v -> v.doSelectAllAction());
  }

  @Bean
  public DepanFxSceneMenuContribution nodeListEditClearSelection() {

    return new DepanFxSceneMenuContribution.Action<DepanFxNodeListViewer>(
        DepanFxSceneMenuItems.SELECTION_NONE_ITEM,
        DepanFxNodeListViewer.class,
        v -> v.doClearSelectionAction());
  }

  @Bean
  public DepanFxSceneMenuContribution nodeListEditInvertSection() {

    return new DepanFxSceneMenuContribution.Action<DepanFxNodeListViewer>(
        DepanFxSceneMenuItems.SELECTION_INVERT_ITEM,
        DepanFxNodeListViewer.class,
        v -> v.doInvertSelectionAction());
  }

  private static class NodeListResourceContribution
      extends DepanFxResourceRegistry.Principal<DepanFxNodeList>
      implements DepanFxResourceRegistry.Panel {

    public NodeListResourceContribution() {
      super(
          OPEN_AS_LIST_LABEL,
          DepanFxNodeList.class,
          DepanFxNodeList.NODE_LIST_EXT,
          OPEN_AS_LIST_ORDER_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxNodeList> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      throw new DepanFxResourceRegistry.UseOpenPanelException(this);
    }

    @Override
    public void openPanel(DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc, DepanFxProjectDocument document) {
      workspace.getWorkspaceResource(document, DepanFxNodeList.class)
        .ifPresent(r -> addNodeListPanelToScene(workspace, sceneSrvc, r));
    }

    private static void addNodeListPanelToScene(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc,
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {

      DepanFxNodeListViewer viewer = new DepanFxNodeListViewer(
          DepanFxWorkspaceFactory.buildDocTitle(nodeListRsrc.getDocument()),
          workspace,
          sceneSrvc.getDialogRunner(),
          nodeListRsrc,
          getTableViewResource(workspace, nodeListRsrc));

      sceneSrvc.addViewer(viewer);
    }
  }

  private static class GraphDocResourceContribution
      extends DepanFxResourceRegistry.Principal<GraphDocument>
      implements DepanFxResourceRegistry.Panel {

    public GraphDocResourceContribution() {
      super(
          OPEN_GRAPH_AS_LIST_LABEL,
          GraphDocument.class,
          GraphDocPersistenceContribution.EXTENSION,
          OPEN_GRAPH_AS_LIST_ORDER_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<GraphDocument> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      throw new DepanFxResourceRegistry.UseOpenPanelException(this);
    }

    @Override
    public void openPanel(DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc, DepanFxProjectDocument document) {
      workspace.getWorkspaceResource(document, GraphDocument.class)
        .ifPresent(r -> addGraphDocViewToScene(workspace, sceneSrvc, r));
    }

    private static void addGraphDocViewToScene(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc,
        DepanFxWorkspaceResource<GraphDocument> graphRsrc) {

      DepanFxNodeList nodeList = DepanFxNodeLists.buildNodeList(graphRsrc);
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc =
          workspace.addScratchResource(nodeList);

      String viewerTitle = DepanFxWorkspaceFactory.buildDocTitle(
          graphRsrc.getDocument()) + " nodes";

      DepanFxNodeListViewer viewer = new DepanFxNodeListViewer(
          viewerTitle, workspace, sceneSrvc.getDialogRunner(),
          nodeListRsrc, getTableViewResource(workspace, nodeListRsrc));

      sceneSrvc.addViewer(viewer);
    }
  }

  private static DepanFxWorkspaceResource<DepanFxNodeListTableViewData>
  getTableViewResource(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {

    String modelContextKey = nodeListRsrc.getResource().getGraphDocResource()
        .getResource().getContextModelId().getContextModelKey();
    Path contextViewPath = DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
        .resolve(modelContextKey)
        .resolve(DepanFxNodeListTableViewData.TABLE_VIEW_CONTEXT_RESOURCE_NAME);

    // If the context view path does not provide a valid resource,
    // use the member view.
    return
        DepanFxProjects.getBuiltIn(
             workspace,  DepanFxNodeListTableViewData.class, contextViewPath)
        .orElseGet(() ->
        DepanFxProjects.getBuiltIn(
            workspace,  DepanFxNodeListTableViewData.class,
            DepanFxNodeListViewBuiltIns.MEMBER_TABLE_VIEW_PATH).get());
  }
}
