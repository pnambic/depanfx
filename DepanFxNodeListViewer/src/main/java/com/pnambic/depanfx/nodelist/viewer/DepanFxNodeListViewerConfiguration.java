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
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersDialogRegistry;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewBuiltIns;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.viewdata.DepanFxNodeListViewerData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistryContribution;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
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
  public DepanFxResourceRegistryContribution<DepanFxNodeListTableViewData>
  TableViewContribution() {
    return new TableViewResourceContribution();
  }

  @Bean
  public DepanFxResourceRegistryContribution<DepanFxNodeList>
  nodeListAsListResourceContribution(
      DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxNodeFiltersDialogRegistry filterDialogRegistry) {
    return new NodeListResourceContribution(
        columnRegistry, infoRegistry, matcherRegistry,
        filterRegistry, filterDialogRegistry);
  }

  @Bean
  public DepanFxResourceRegistryContribution<GraphDocument>
  graphDocAsListResourceContribution(
      DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxNodeFiltersDialogRegistry filterDialogRegistry) {
    return new GraphDocResourceContribution(
        columnRegistry, infoRegistry, matcherRegistry,
        filterRegistry, filterDialogRegistry);
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
      extends DepanFxResourceRegistryContribution.Principal<DepanFxNodeListTableViewData>
      implements DepanFxResourceRegistryContribution.Dialog<DepanFxNodeListTableViewData> {

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
    public void runDialog(DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxNodeListTableViewData> dialogRsrc) {

      LOG.info("No editor for DepanFxNodeListTableViewData");
      // DepanFxNodeListTableViewDialog.runEditDialog(dialogRunner, tableRsrc);

      // Without a node list, can't open a DepanFxNodeListTableViewData
      // into a panel.
    }
  }

  /**
   * Base contribution for node lists and graph docs into node list viewers.
   */
  private static abstract class NodeViewerContribution<T>
      extends DepanFxResourceRegistryContribution.Principal<T>
      implements DepanFxResourceRegistryContribution.Panel<T> {

    private final DepanFxColumnRegistry columnRegistry;

    private final DepanFxInfoRegistry infoRegistry;

    private final DepanFxLinkMatchersRegistry matcherRegistry;

    private final DepanFxNodeFiltersRegistry filterRegistry;

    private final DepanFxNodeFiltersDialogRegistry filterDialogRegistry;

    public NodeViewerContribution(
        String resourceLabel,
        Class<T> dataType,
        String fileExt,
        String orderKey,
        DepanFxColumnRegistry columnRegistry,
        DepanFxInfoRegistry infoRegistry,
        DepanFxLinkMatchersRegistry matcherRegistry,
        DepanFxNodeFiltersRegistry filterRegistry,
        DepanFxNodeFiltersDialogRegistry filterDialogRegistry) {
      super(resourceLabel, dataType, fileExt, orderKey);
      this.columnRegistry = columnRegistry;
      this.infoRegistry = infoRegistry;
      this.matcherRegistry = matcherRegistry;
      this.filterRegistry = filterRegistry;
      this.filterDialogRegistry = filterDialogRegistry;
    }

    @Override
    public void openPanel(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrcv,
        DepanFxWorkspaceResource<T> panelRsrc) {

      DepanFxNodeListViewerData viewInfo =
          prepareNodeListViewer(workspace, panelRsrc);

      // Confirm with user and start view
      DepanFxNodeListViewerDialog.runEditDialog(
          workspace, sceneSrcv.getDialogRunner(), viewInfo)
          .ifPresent(v -> startNodeListViewer(workspace, sceneSrcv, v));
    }

    protected abstract DepanFxNodeListViewerData prepareNodeListViewer(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<T> viewRsrc);

    private void startNodeListViewer(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc,
        DepanFxNodeListViewerData viewInfo) {

      DepanFxNodeListViewer viewer = new DepanFxNodeListViewer(
          viewInfo.getViewerTitle(),
          workspace, sceneSrvc.getDialogRunner(),
          columnRegistry, infoRegistry, matcherRegistry,
          filterRegistry, filterDialogRegistry);

      sceneSrvc.addViewer(viewer);
      viewer.initFromNodeListResource(
          viewInfo.getNodeListRsrc(),
          viewInfo.getTableViewRsrc());
    }
  }

  private static class NodeListResourceContribution
      extends NodeViewerContribution<DepanFxNodeList>
      implements DepanFxResourceRegistryContribution.Dialog<DepanFxNodeList> {

    public NodeListResourceContribution(
        DepanFxColumnRegistry columnRegistry,
        DepanFxInfoRegistry infoRegistry,
        DepanFxLinkMatchersRegistry matcherRegistry,
        DepanFxNodeFiltersRegistry filterRegistry,
        DepanFxNodeFiltersDialogRegistry filterDialogRegistry) {
      super(
          OPEN_AS_LIST_LABEL,
          DepanFxNodeList.class,
          DepanFxNodeList.NODE_LIST_EXT,
          OPEN_AS_LIST_ORDER_KEY,
          columnRegistry, infoRegistry, matcherRegistry,
          filterRegistry, filterDialogRegistry);
    }

    @Override // DepanFxResourceRegistryContribution.Dialog
    public void runDialog(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
      DepanFxSaveNodeListDialog.runSaveNodeList(dialogRunner, nodeListRsrc);
    }

    @Override // NodeViewerContribution
    protected DepanFxNodeListViewerData prepareNodeListViewer(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {

      // Infer missing view properties from node list
      String viewTitle =
          DepanFxWorkspaceFactory.buildDocTitle(nodeListRsrc.getDocument());

      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc =
          getTableViewResource(
              workspace, nodeListRsrc.getResource().getGraphDocResource());

      return new DepanFxNodeListViewerData(
          viewTitle, nodeListRsrc, tableViewRsrc);
    }
  }

  private static class GraphDocResourceContribution
      extends NodeViewerContribution<GraphDocument> {

    public GraphDocResourceContribution(
        DepanFxColumnRegistry columnRegistry,
        DepanFxInfoRegistry infoRegistry,
        DepanFxLinkMatchersRegistry matcherRegistry,
        DepanFxNodeFiltersRegistry filterRegistry,
        DepanFxNodeFiltersDialogRegistry filterDialogRegistry) {
      super(
          OPEN_GRAPH_AS_LIST_LABEL,
          GraphDocument.class,
          GraphDocPersistenceContribution.EXTENSION,
          OPEN_GRAPH_AS_LIST_ORDER_KEY,
          columnRegistry, infoRegistry, matcherRegistry,
          filterRegistry, filterDialogRegistry);
    }

    @Override // NodeViewerContribution
    protected DepanFxNodeListViewerData prepareNodeListViewer(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<GraphDocument> graphDocRsrc) {

    // Infer missing view properties from node list
    String viewTitle = DepanFxWorkspaceFactory.buildDocTitle(
        graphDocRsrc.getDocument()) + " nodes";

    DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc =
        workspace.addScratchResource(
            DepanFxNodeLists.buildNodeList(graphDocRsrc));

    DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc =
        getTableViewResource(workspace, graphDocRsrc);

    return new DepanFxNodeListViewerData(
        viewTitle, nodeListRsrc, tableViewRsrc);
    }
  }

  private static DepanFxWorkspaceResource<DepanFxNodeListTableViewData>
  getTableViewResource(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceResource<GraphDocument> graphRsrc) {

    String modelContextPath =
        graphRsrc.getResource().getContextModelId().getContextModelPath();
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
