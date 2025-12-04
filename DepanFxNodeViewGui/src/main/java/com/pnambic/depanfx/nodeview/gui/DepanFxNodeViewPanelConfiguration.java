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

import com.pnambic.depanfx.edgematchers.gui.DepanFxEdgeMatcherDialogRegistry;
import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchersRegistry;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersDialogRegistry;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.nodeview.viewdata.DepanFxNodeViewPanelInitData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistryContribution;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Map;

@Configuration
public class DepanFxNodeViewPanelConfiguration {

  public static final String NODE_VIEW_LABEL = "Node View";

  public static final String NODE_VIEW_KEY = "Node View";

  public static final String OPEN_NODE_LIST_AS_VIEW_LABEL =
      "as Node View";

  public static final String OPEN_NODE_LIST_AS_VIEW_KEY =
      "Node List as Node View";

  public static final String OPEN_GRAPH_DOC_AS_VIEW_LABEL =
      "as Node View";

  public static final String OPEN_GRAPH_DOC_AS_VIEW_KEY =
      "Graph Doc as Node View";

  public static final String LINK_DISPLAY_LABEL = "Link Display";

  public static final String LINK_DISPLAY_KEY = "Link Display";

  public static final String NODE_DISPLAY_LABEL = "Node Display";

  public static final String NODE_DISPLAY_KEY = "Node Display";

  /////////////////////////////////////
  // Open various resources as node views

  @Bean
  public DepanFxResourceRegistryContribution<DepanFxNodeViewData>
  nodeViewAsViewResourceContribution(
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxNodeFiltersDialogRegistry filterDialogRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry) {
    return new NodeViewAsViewResourceContribution(
        layoutRegistry, filterRegistry, filterDialogRegistry,
        matcherRegistry, matcherDialogRegistry);
  }

  @Bean
  public DepanFxResourceRegistryContribution<DepanFxNodeList>
  nodeListAsViewResourceContribution(
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxNodeFiltersDialogRegistry filterDialogRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry,
      DepanFxInfoRegistry infoRegistry) {
    return new NodeListAsViewResourceContribution(
        layoutRegistry, filterRegistry, filterDialogRegistry,
        matcherRegistry, matcherDialogRegistry, infoRegistry);
  }

  @Bean
  public DepanFxResourceRegistryContribution<GraphDocument>
  graphDocAsViewResourceContribution(
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxNodeFiltersDialogRegistry filterDialogRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry,
      DepanFxInfoRegistry infoRegistry) {
    return new GraphDocAsViewResourceContribution(
        layoutRegistry, filterRegistry, filterDialogRegistry,
        matcherRegistry, matcherDialogRegistry, infoRegistry);
  }

  /////////////////////////////////////
  // Open view rendering resources

  @Bean
  public DepanFxResourceRegistryContribution<DepanFxNodeViewLinkDisplayData>
  linkDisplayResourceContribution() {
    return new LinkDisplayResourceContribution();
  }

  @Bean
  public DepanFxResourceRegistryContribution<DepanFxNodeViewNodeDisplayData>
  nodeDisplayResourceContribution() {
    return new NodeDisplayResourceContribution();
  }

  /////////////////////////////////////
  // Selection Menu Contributions

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

  /////////////////////////////////////
  // Node View internal display resources

  private static class LinkDisplayResourceContribution
      extends DepanFxResourceRegistryContribution.Principal<DepanFxNodeViewLinkDisplayData>
      implements DepanFxResourceRegistryContribution.Dialog<DepanFxNodeViewLinkDisplayData> {

    public LinkDisplayResourceContribution() {
      super(LINK_DISPLAY_LABEL,
          DepanFxNodeViewLinkDisplayData.class,
          DepanFxNodeViewLinkDisplayData.NODE_VIEW_LINK_DISPLAY_EXT,
          LINK_DISPLAY_KEY);
    }

    @Override
    public void runDialog(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc) {
      DepanFxNodeViewLinkDisplayDialog.runEditDialog(displayRsrc, dialogRunner);
    }
  }

  private static class NodeDisplayResourceContribution
      extends DepanFxResourceRegistryContribution.Principal<DepanFxNodeViewNodeDisplayData>
      implements DepanFxResourceRegistryContribution.Dialog<DepanFxNodeViewNodeDisplayData> {

    public NodeDisplayResourceContribution() {
      super(NODE_DISPLAY_LABEL,
          DepanFxNodeViewNodeDisplayData.class,
          DepanFxNodeViewNodeDisplayData.NODE_VIEW_NODE_DISPLAY_EXT,
          NODE_DISPLAY_KEY);
    }

    @Override
    public void runDialog(DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> displayRsrc) {
      DepanFxNodeViewNodeDisplayDialog.runEditDialog(displayRsrc, dialogRunner);
    }
  }

  /////////////////////////////////////
  // Supported Node View resources

  private static class NodeViewAsViewResourceContribution
      extends DepanFxResourceRegistryContribution.Principal<DepanFxNodeViewData>
      implements DepanFxResourceRegistryContribution.Panel<DepanFxNodeViewData> {

    private final DepanFxNodeLayoutRegistry layoutRegistry;

    private final DepanFxNodeFiltersRegistry filterRegistry;

    private final DepanFxNodeFiltersDialogRegistry filterDialogRegistry;

    private final DepanFxLinkMatchersRegistry matcherRegistry;

    private final DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry;

    public NodeViewAsViewResourceContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry,
        DepanFxNodeFiltersDialogRegistry filterDialogRegistry,
        DepanFxLinkMatchersRegistry matcherRegistry,
        DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry) {
      super(NODE_VIEW_LABEL,
          DepanFxNodeViewData.class,
          DepanFxNodeViewData.NODE_VIEW_TOOL_EXT,
          NODE_VIEW_KEY);
      this.layoutRegistry = layoutRegistry;
      this.filterRegistry = filterRegistry;
      this.filterDialogRegistry = filterDialogRegistry;
      this.matcherRegistry = matcherRegistry;
      this.matcherDialogRegistry = matcherDialogRegistry;
    }

    @Override
    public void openPanel(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc,
        DepanFxWorkspaceResource<DepanFxNodeViewData> panelRsrc) {

      DepanFxNodeViewPanel viewPanel = new DepanFxNodeViewPanel(
          workspace, layoutRegistry, filterRegistry, filterDialogRegistry,
          matcherRegistry, matcherDialogRegistry, panelRsrc);

      sceneSrvc.addViewer(viewPanel);
    }
  }

  private static abstract class AdditionalAsViewResourceContribution<T>
      extends DepanFxResourceRegistryContribution.Additional<T>
      implements DepanFxResourceRegistryContribution.Panel<T> {

    private final DepanFxNodeLayoutRegistry layoutRegistry;

    private final DepanFxNodeFiltersRegistry filterRegistry;

    private final DepanFxNodeFiltersDialogRegistry filterDialogRegistry;

    private final DepanFxLinkMatchersRegistry matcherRegistry;

    private final DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry;

    private AdditionalAsViewResourceContribution(
        String resourceLabel,
        Class<T> dataType,
        String fileExt,
        String orderKey,
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry,
        DepanFxNodeFiltersDialogRegistry filterDialogRegistry,
        DepanFxLinkMatchersRegistry matcherRegistry,
        DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry) {
      super(resourceLabel, dataType, fileExt, orderKey);
      this.layoutRegistry = layoutRegistry;
      this.filterRegistry = filterRegistry;
      this.filterDialogRegistry = filterDialogRegistry;
      this.matcherRegistry = matcherRegistry;
      this.matcherDialogRegistry = matcherDialogRegistry;
    }

    @Override
    public void openPanel(DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrcv,
        DepanFxWorkspaceResource<T> panelRsrc) {

      ContextModelId modelId =
          getGraphDocResource(panelRsrc).getResource().getContextModelId();
      DepanFxWorkspaceResource<Object> layoutRsrc =
          DepanFxNodeViews.getContextLayout(workspace, modelId).orElse(null);

      DepanFxNodeViewPanelInitData initInfo =
          new DepanFxNodeViewPanelInitData(layoutRsrc);

      DepanFxNodeViewInitDialog.runEditDialog(
          sceneSrcv.getDialogRunner(), initInfo)
          .ifPresent(i -> startNodeViewPanel(workspace, sceneSrcv, panelRsrc, i));
    }

    protected abstract DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<T> rsrc,
        Map<GraphNode, DepanFxNodeLocationData> locations);

    protected abstract DepanFxWorkspaceResource<GraphDocument>
    getGraphDocResource(
        DepanFxWorkspaceResource<T> rsrc);

    protected abstract Collection<GraphNode> getNodes(
        DepanFxWorkspaceResource<T> rsrc);

    private void startNodeViewPanel(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrvc,
        DepanFxWorkspaceResource<T> panelRsrc,
        DepanFxNodeViewPanelInitData initInfo) {

      Map<GraphNode, DepanFxNodeLocationData> locations =
      DepanFxNodeViews.buildNodeLocations(
          layoutRegistry,
          getGraphDocResource(panelRsrc),
          getNodes(panelRsrc),
          initInfo.getLayoutRsrc());

      DepanFxNodeViewData nodeViewInfo =
          getNodeViewData(workspace, panelRsrc, locations);
      DepanFxWorkspaceResource<DepanFxNodeViewData> nodeViewRsrc =
          workspace.addScratchResource(nodeViewInfo);

      DepanFxNodeViewPanel viewPanel = new DepanFxNodeViewPanel(
          workspace, layoutRegistry, filterRegistry, filterDialogRegistry,
          matcherRegistry, matcherDialogRegistry, nodeViewRsrc);

      sceneSrvc.addViewer(viewPanel);
    }
  }

  private static class NodeListAsViewResourceContribution
      extends AdditionalAsViewResourceContribution<DepanFxNodeList> {

    private NodeListAsViewResourceContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry,
        DepanFxNodeFiltersDialogRegistry filterDialogRegistry,
        DepanFxLinkMatchersRegistry matcherRegistry,
        DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry,
        DepanFxInfoRegistry infoRegistry) {
      super(
          OPEN_NODE_LIST_AS_VIEW_LABEL,
          DepanFxNodeList.class,
          DepanFxNodeList.NODE_LIST_EXT,
          OPEN_NODE_LIST_AS_VIEW_KEY,
          layoutRegistry,
          filterRegistry,
          filterDialogRegistry,
          matcherRegistry,
          matcherDialogRegistry);
    }

    @Override
    protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListResource,
        Map<GraphNode, DepanFxNodeLocationData> locations) {

      return DepanFxNodeViews.fromNodeList(
          workspace, nodeListResource, locations);
    }

    @Override
    protected DepanFxWorkspaceResource<GraphDocument> getGraphDocResource(
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
      return nodeListRsrc.getResource().getGraphDocResource();
    }

    @Override
    protected Collection<GraphNode> getNodes(
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
      return nodeListRsrc.getResource().getNodes();
    }
  }

  private static class GraphDocAsViewResourceContribution
      extends AdditionalAsViewResourceContribution<GraphDocument> {

    private GraphDocAsViewResourceContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry,
        DepanFxNodeFiltersDialogRegistry filterDialogRegistry,
        DepanFxLinkMatchersRegistry matcherRegistry,
        DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry,
        DepanFxInfoRegistry infoRegistry) {
      super(
          OPEN_GRAPH_DOC_AS_VIEW_LABEL,
          GraphDocument.class,
          GraphDocPersistenceContribution.EXTENSION,
          OPEN_GRAPH_DOC_AS_VIEW_KEY,
          layoutRegistry,
          filterRegistry,
          filterDialogRegistry,
          matcherRegistry,
          matcherDialogRegistry);
    }

    @Override
    protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<GraphDocument> graphDocResource,
        Map<GraphNode, DepanFxNodeLocationData> locations) {

      return DepanFxNodeViews.fromGraphDocument(
          workspace, graphDocResource, locations);
    }

    @Override
    protected DepanFxWorkspaceResource<GraphDocument> getGraphDocResource(
        DepanFxWorkspaceResource<GraphDocument> graphDocRsrc) {
      return graphDocRsrc;
    }

    @Override
    protected Collection<GraphNode> getNodes(
        DepanFxWorkspaceResource<GraphDocument> graphDocRsrc) {
      return graphDocRsrc.getResource().getGraph().getGraphNodes();
    }
  }
}
