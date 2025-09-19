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

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchersRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewBuiltIns;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.sections.folds.NodeListFoldController;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeFoldController;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class DepanFxNodeListViewerConfiguration {

  public static final String OPEN_LIST_VIEW_LABEL = "List View";

  public static final String OPEN_LIST__VIEW_ORDER_KEY = "List View";

  public static final String OPEN_AS_LIST_LABEL = "as Node List";

  public static final String OPEN_AS_LIST_ORDER_KEY = "Node List";

  public static final String OPEN_GRAPH_AS_LIST_LABEL = "as Node List";

  public static final String OPEN_GRAPH_AS_LIST_ORDER_KEY =
      "Graph Doc as Node List";

  @Autowired
  public DepanFxNodeListViewerConfiguration() {
  }

  @Bean
  public DepanFxResourceRegistry.Contribution TableViewContribution() {
    return new TableViewResourceContribution();
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  nodeListAsListResourceContribution(
      DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry) {
    return new NodeListResourceContribution(
        columnRegistry, infoRegistry, matcherRegistry);
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  graphDocAsListResourceContribution(
      DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry) {
    return new GraphDocResourceContribution(
        columnRegistry, infoRegistry, matcherRegistry);
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

  private static class TableViewResourceContribution
      extends DepanFxResourceRegistry.Principal<DepanFxNodeListTableViewData> {

    private static final Logger LOG =
        LoggerFactory.getLogger(TableViewResourceContribution.class);

    public TableViewResourceContribution() {
      super(
          OPEN_LIST_VIEW_LABEL + " [Down]",
          DepanFxNodeListTableViewData.class,
          DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_EXT,
          OPEN_LIST_VIEW_LABEL);
    }

    @Override
    protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxNodeListTableViewData> wkspRsrc) {

      LOG.info("No editor for DepanFxNodeListTableViewData");
      // DepanFxNodeListTableViewDialog.runEditDialog(dialogRunner, tableRsrc);

      // Without a node list, can't open a DepanFxNodeListTableViewData
      // into a panel.
    }
  }

  private static class NodeListResourceContribution
      extends DepanFxResourceRegistry.Principal<DepanFxNodeList>
      implements DepanFxResourceRegistry.Panel {

    private final DepanFxLinkMatchersRegistry matcherRegistry;

    private final DepanFxColumnRegistry columnRegistry;

    private final DepanFxInfoRegistry infoRegistry;

    public NodeListResourceContribution(
        DepanFxColumnRegistry columnRegistry,
        DepanFxInfoRegistry infoRegistry,
        DepanFxLinkMatchersRegistry matcherRegistry) {
      super(
          OPEN_AS_LIST_LABEL,
          DepanFxNodeList.class,
          DepanFxNodeList.NODE_LIST_EXT,
          OPEN_AS_LIST_ORDER_KEY);
      this.columnRegistry = columnRegistry;
      this.infoRegistry = infoRegistry;
      this.matcherRegistry = matcherRegistry;
    }

    @Override
    protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
      DepanFxSaveNodeListDialog.runSaveNodeList(dialogRunner, nodeListRsrc);
    }

    @Override
    public void openPanel(DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc,
        DepanFxProjectDocument document) {

      workspace.getWorkspaceResource(document, DepanFxNodeList.class)
          .ifPresent(r -> addNodeListPanelToScene(
              workspace, sceneSrvc, r));
    }

    private void addNodeListPanelToScene(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc,
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {

      DepanFxNodeFoldController nodeFolding =
          new NodeListFoldController(
              workspace, nodeListRsrc.getResource().getGraphDocResource());

      DepanFxNodeListViewer viewer = new DepanFxNodeListViewer(
          DepanFxWorkspaceFactory.buildDocTitle(nodeListRsrc.getDocument()),
          workspace,
          sceneSrvc.getDialogRunner(),
          columnRegistry,
          infoRegistry,
          matcherRegistry,
          nodeFolding,
          nodeListRsrc,
          getTableViewResource(workspace, nodeListRsrc));

      sceneSrvc.addViewer(viewer);
    }
  }

  private static class GraphDocResourceContribution
      extends DepanFxResourceRegistry.Principal<GraphDocument>
      implements DepanFxResourceRegistry.Panel {

    private final DepanFxLinkMatchersRegistry matcherRegistry;

    private final DepanFxColumnRegistry columnRegistry;

    private final DepanFxInfoRegistry infoRegistry;

    public GraphDocResourceContribution(
        DepanFxColumnRegistry columnRegistry,
        DepanFxInfoRegistry infoRegistry,
        DepanFxLinkMatchersRegistry matcherRegistry) {
      super(
          OPEN_GRAPH_AS_LIST_LABEL,
          GraphDocument.class,
          GraphDocPersistenceContribution.EXTENSION,
          OPEN_GRAPH_AS_LIST_ORDER_KEY);

      this.columnRegistry = columnRegistry;
      this.infoRegistry = infoRegistry;
      this.matcherRegistry = matcherRegistry;
    }

    @Override
    protected void runDialog(DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<GraphDocument> wkspRsrc) {
      throw new DepanFxResourceRegistry.UseOpenPanelException(this);
    }

    @Override
    public void openPanel(DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc,
        DepanFxProjectDocument document) {

      workspace.getWorkspaceResource(document, GraphDocument.class)
          .ifPresent(r -> addGraphDocViewToScene( workspace, sceneSrvc, r));
    }

    private void addGraphDocViewToScene(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc,
        DepanFxWorkspaceResource<GraphDocument> graphRsrc) {

      DepanFxNodeList nodeList = DepanFxNodeLists.buildNodeList(graphRsrc);
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc =
          workspace.addScratchResource(nodeList);

      String viewerTitle = DepanFxWorkspaceFactory.buildDocTitle(
          graphRsrc.getDocument()) + " nodes";

      DepanFxNodeFoldController nodeFolding =
          new NodeListFoldController(workspace, graphRsrc);

      DepanFxNodeListViewer viewer = new DepanFxNodeListViewer(
          viewerTitle, workspace, sceneSrvc.getDialogRunner(),
          columnRegistry, infoRegistry, matcherRegistry, nodeFolding,
          nodeListRsrc, getTableViewResource(workspace, nodeListRsrc));

      sceneSrvc.addViewer(viewer);
    }
  }

  private static DepanFxWorkspaceResource<DepanFxNodeListTableViewData>
  getTableViewResource(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {

    String modelContextPath = nodeListRsrc.getResource().getGraphDocResource()
        .getResource().getContextModelId().getContextModelPath();
    Path contextViewPath = DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
        .resolve(modelContextPath)
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
