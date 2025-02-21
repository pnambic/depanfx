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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepanFxNodeViewPanelConfiguration {

  public static final String NODE_VIEW_LABEL = "Node View";

  public static final String NODE_VIEW_KEY = "Node View";

  public static final String OPEN_NODE_LIST_AS_VIEW_LABEL =
      "Open Node List as Node View";

  public static final String OPEN_NODE_LIST_AS_VIEW_KEY =
      "Node List as Node View";

  public static final String OPEN_GRAPH_DOC_AS_VIEW_LABEL =
      "Open Graph Doc as Node View";

  public static final String OPEN_GRAPH_DOC_AS_VIEW_KEY =
      "Graph Doc as View";

  private final DepanFxNodeLayoutRegistry layoutRegistry;

  private final DepanFxNodeFiltersRegistry filterRegistry;

  @Autowired
  public DepanFxNodeViewPanelConfiguration(
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry) {
    this.layoutRegistry = layoutRegistry;
    this.filterRegistry = filterRegistry;
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  nodeViewAsViewResourceContribution() {
    return new NodeViewAsViewResourceContribution(
        layoutRegistry, filterRegistry);
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  nodeListAsViewResourceContribution() {
    return new NodeListAsViewResourceContribution(
        layoutRegistry, filterRegistry);
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  graphDocAsViewResourceContribution() {
    return new GraphDocAsViewResourceContribution(
        layoutRegistry, filterRegistry);
  }

  @Bean
  public DepanFxSceneMenuContribution nodeViewEditSelectAll() {

    return new DepanFxSceneMenuContribution.Action<DepanFxNodeViewPanel>(
        DepanFxSceneMenuItems.SELECTION_ALL_ITEM,
        DepanFxNodeViewPanel.class,
        v -> v.doSelectAllAction());
  }

  @Bean
  public DepanFxSceneMenuContribution nodeViewEditClearSelection() {

    return new DepanFxSceneMenuContribution.Action<DepanFxNodeViewPanel>(
        DepanFxSceneMenuItems.SELECTION_NONE_ITEM,
        DepanFxNodeViewPanel.class,
        v -> v.doClearSelectionAction());
  }

  @Bean
  public DepanFxSceneMenuContribution nodeViewEditInvertSection() {

    return new DepanFxSceneMenuContribution.Action<DepanFxNodeViewPanel>(
        DepanFxSceneMenuItems.SELECTION_INVERT_ITEM,
        DepanFxNodeViewPanel.class,
        v -> v.doInvertSelectionAction());
  }

  private static class NodeViewAsViewResourceContribution
      extends DepanFxResourceRegistry.Principal<DepanFxNodeViewPanel>
      implements DepanFxResourceRegistry.Panel {

    private final DepanFxNodeLayoutRegistry layoutRegistry;

    private final DepanFxNodeFiltersRegistry filterRegistry;

    public NodeViewAsViewResourceContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry) {
      super(NODE_VIEW_LABEL,
          DepanFxNodeViewPanel.class,
          DepanFxNodeViewData.NODE_VIEW_TOOL_EXT,
          NODE_VIEW_KEY);
      this.layoutRegistry = layoutRegistry;
      this.filterRegistry = filterRegistry;
    }

    @Override
    public void openPanel(DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrcv, DepanFxProjectDocument document) {
      workspace.getWorkspaceResource(document, DepanFxNodeViewData.class)
          .ifPresent(r ->
              addNodeViewPanelToScene(
                  workspace, sceneSrcv, r, layoutRegistry, filterRegistry));
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxNodeViewPanel> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      throw new DepanFxResourceRegistry.UseOpenPanelException(this);
    }
  }

  private static abstract class AdditionalAsViewResourceContribution<T>
      extends DepanFxResourceRegistry.Additional<T>
      implements DepanFxResourceRegistry.Panel {

    private final DepanFxNodeLayoutRegistry layoutRegistry;

    private final DepanFxNodeFiltersRegistry filterRegistry;

    private AdditionalAsViewResourceContribution(
        String resourceLabel,
        Class<T> dataType,
        String fileExt,
        String orderKey,
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry) {
      super(resourceLabel, dataType, fileExt, orderKey);
      this.layoutRegistry = layoutRegistry;
      this.filterRegistry = filterRegistry;
    }

    @Override
    public void openPanel(DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrcv, DepanFxProjectDocument document) {
      workspace.getWorkspaceResource(document, GraphDocument.class)
          .map(r -> getNodeViewData(workspace, r, layoutRegistry))
          .map(d -> workspace.addScratchResource(d))
          .ifPresent(r ->
              addNodeViewPanelToScene(
                  workspace, sceneSrcv, r, layoutRegistry, filterRegistry));
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<T> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      throw new DepanFxResourceRegistry.UseOpenPanelException(this);
    }

    protected abstract DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<?> rsrc,
        DepanFxNodeLayoutRegistry layoutRegistry);
  }

  private static class NodeListAsViewResourceContribution
      extends AdditionalAsViewResourceContribution<DepanFxNodeList> {

    private NodeListAsViewResourceContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry) {
      super(
          OPEN_NODE_LIST_AS_VIEW_LABEL,
          DepanFxNodeList.class,
          DepanFxNodeList.NODE_LIST_EXT,
          OPEN_NODE_LIST_AS_VIEW_KEY,
          layoutRegistry,
          filterRegistry);
    }

    @Override
    protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<?> rsrc,
        DepanFxNodeLayoutRegistry layoutRegistry) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListResource =
          (DepanFxWorkspaceResource<DepanFxNodeList>) rsrc;

      return DepanFxNodeViews.fromNodeList(
          nodeListResource, workspace, layoutRegistry);
    }
  }

  private static class GraphDocAsViewResourceContribution
      extends AdditionalAsViewResourceContribution<GraphDocument> {

    private GraphDocAsViewResourceContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry) {
      super(
          OPEN_GRAPH_DOC_AS_VIEW_LABEL,
          GraphDocument.class,
          GraphDocPersistenceContribution.EXTENSION,
          OPEN_GRAPH_DOC_AS_VIEW_LABEL,
          layoutRegistry,
          filterRegistry);
    }

    @Override
    protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<?> rsrc,
        DepanFxNodeLayoutRegistry layoutRegistry) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<GraphDocument> graphDocResource =
          (DepanFxWorkspaceResource<GraphDocument>) rsrc;

      return DepanFxNodeViews.fromGraphDocument(
          graphDocResource, workspace, layoutRegistry);
    }
  }

  private static void addNodeViewPanelToScene(
      DepanFxWorkspace workspace,
      DepanFxSceneService sceneSrvc,
      DepanFxWorkspaceResource<DepanFxNodeViewData> nodeViewRsrc,
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry) {
    DepanFxNodeViewPanel viewPanel = new DepanFxNodeViewPanel(
        workspace, layoutRegistry, filterRegistry, nodeViewRsrc);
    sceneSrvc.addViewer(viewPanel);
  }
}
