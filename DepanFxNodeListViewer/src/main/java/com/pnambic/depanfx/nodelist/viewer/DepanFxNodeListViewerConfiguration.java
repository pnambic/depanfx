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
import com.pnambic.depanfx.nodelist.builtins.DepanFxNodeListViewBuiltIns;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxAnalysisExtMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeListViewerConfiguration {

  public static final String NODE_LIST_KEY = "Node List Key";

  public static final String OPEN_AS_LIST = "Open as Node List";

  @Autowired
  public DepanFxNodeListViewerConfiguration() {
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution graphAsListExtMenu() {
    return new GraphAsListContribution();
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution nodeListExtMenu() {
    return new NodeListContribution();
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

  private static class GraphAsListContribution
      implements DepanFxAnalysisExtMenuContribution {

    private static Logger LOG =
        LoggerFactory.getLogger(GraphAsListContribution.class);

    @Override
    public boolean acceptsExt(String ext) {
      return "dgi".equals(ext);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner, DepanFxSceneController scene,
        Cell<DepanFxWorkspaceMember> cell, String ext,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      Path docPath = member.getMemberPath();
      DepanFxResourcePerspectives.installOnOpen(cell, docPath,
          p -> runOpenAsListAction(scene, dialogRunner, workspace, p));
      builder.appendActionItem(
          OPEN_AS_LIST,
          e -> runOpenAsListAction(scene, dialogRunner, workspace, docPath));
    }

    private void runOpenAsListAction(
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace,
        Path docPath) {
      try {
        workspace.toProjectDocument(docPath.toUri())
            .flatMap(r ->
                workspace.getWorkspaceResource(r, GraphDocument.class))
            .ifPresent(nl -> {
                addGraphDocViewToScene(workspace, dialogRunner, scene, nl);
        });
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open node list for {}",
            docPath.toUri(), errCaught);
      }
    }

    @Override
    public String getOrderKey() {
      return NODE_LIST_KEY;
    }
  }

  private static class NodeListContribution
      implements DepanFxAnalysisExtMenuContribution {

    private static Logger LOG =
        LoggerFactory.getLogger(NodeListContribution.class);

    @Override
    public boolean acceptsExt(String ext) {
      return DepanFxNodeList.NODE_LIST_EXT.equals(ext);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner, DepanFxSceneController scene,
        Cell<DepanFxWorkspaceMember> cell, String ext,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      Path docPath = member.getMemberPath();
      DepanFxResourcePerspectives.installOnOpen(cell, docPath,
          p -> runOpenNodeListAction(workspace, dialogRunner, scene, p));
      builder.appendActionItem(
          OPEN_AS_LIST,
          e -> runOpenNodeListAction(workspace, dialogRunner, scene, docPath));
    }

    private void runOpenNodeListAction(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxSceneController scene,
        Path docPath) {
      try {
        workspace.toProjectDocument(docPath.toUri())
            .flatMap(r ->
                workspace.getWorkspaceResource(r, DepanFxNodeList.class))
            .ifPresent(nl ->
                addNodeListDocViewToScene(workspace, dialogRunner, scene, nl));
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open list view for {}",
            docPath.toUri(), errCaught);
      }
    }

    @Override
    public String getOrderKey() {
      return NODE_LIST_KEY;
    }
  }

  private static void addGraphDocViewToScene(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxSceneController scene,
      DepanFxWorkspaceResource<GraphDocument> graphRsrc) {

    DepanFxNodeList nodeList = DepanFxNodeLists.buildNodeList(graphRsrc);
    DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc =
        workspace.addScratchResource(nodeList);

    String viewerTitle = DepanFxWorkspaceFactory.buildDocTitle(
        graphRsrc.getDocument()) + " nodes";

    addNodeListViewToScene(
        workspace, dialogRunner, scene, viewerTitle, nodeListRsrc);
  }

  private static void addNodeListDocViewToScene(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxSceneController scene,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {

    String viewerTitle =
        DepanFxWorkspaceFactory.buildDocTitle(nodeListRsrc.getDocument());
    addNodeListViewToScene(
        workspace, dialogRunner, scene, viewerTitle, nodeListRsrc);
  }

  private static void addNodeListViewToScene(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxSceneController scene,
      String viewerTitle,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {

    DepanFxNodeListViewer viewer = new DepanFxNodeListViewer(
        viewerTitle, workspace, dialogRunner,
        nodeListRsrc, getTableViewResource(workspace, nodeListRsrc));

    scene.addViewer(viewer);
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
