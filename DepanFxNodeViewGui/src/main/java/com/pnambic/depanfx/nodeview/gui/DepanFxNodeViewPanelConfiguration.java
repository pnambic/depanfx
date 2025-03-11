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

import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

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

  public static final String NODE_POSITION_LABEL = "Position";

  public static final String NODE_POSITION_KEY = "Position";

  public static final String NODE_POSITION_DESCR =
      "Position of nodes in their rendered graph view.";

  /////////////////////////////////////
  // Open various resources as node views

  @Bean
  public DepanFxResourceRegistry.Contribution
  nodeViewAsViewResourceContribution(
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry) {
    return new NodeViewAsViewResourceContribution(
        layoutRegistry, filterRegistry);
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  nodeListAsViewResourceContribution(
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry) {
    return new NodeListAsViewResourceContribution(
        layoutRegistry, filterRegistry);
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
  graphDocAsViewResourceContribution(
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry) {
    return new GraphDocAsViewResourceContribution(
        layoutRegistry, filterRegistry);
  }

  /////////////////////////////////////
  // Open view rendering resources

  @Bean
  public DepanFxResourceRegistry.Contribution
  linkDisplayResourceContribution() {
    return new LinkDisplayResourceContribution();
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
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
  // Node Position Info

  @Bean
  public DepanFxInfoRegistry.Contribution nodePositionsInfoContribution() {
    return new NodePositionInfoContribution();
  }

  /////////////////////////////////////
  // Node View internal display resources

  private static class LinkDisplayResourceContribution
      extends DepanFxResourceRegistry.Principal<DepanFxNodeViewLinkDisplayData> {

    public LinkDisplayResourceContribution() {
      super(LINK_DISPLAY_LABEL,
          DepanFxNodeViewLinkDisplayData.class,
          DepanFxNodeViewLinkDisplayData.NODE_VIEW_LINK_DISPLAY_EXT,
          LINK_DISPLAY_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeViewLinkDisplayDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }

  private static class NodeDisplayResourceContribution
      extends DepanFxResourceRegistry.Principal<DepanFxNodeViewNodeDisplayData> {

    public NodeDisplayResourceContribution() {
      super(NODE_DISPLAY_LABEL,
          DepanFxNodeViewNodeDisplayData.class,
          DepanFxNodeViewNodeDisplayData.NODE_VIEW_NODE_DISPLAY_EXT,
          NODE_DISPLAY_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeViewNodeDisplayDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }

  /////////////////////////////////////
  // Supported Node View resources

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
      loadResource(workspace, document)
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
          OPEN_GRAPH_DOC_AS_VIEW_KEY,
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

  private static class NodePositionInfoContribution extends DepanFxInfoRegistry.Basic {

    public static DepanFxNodeInfoProperty buildPosProperty(
        String toolName, String toolDescription) {
      return new DepanFxNodeInfoProperty(
          toolName, toolDescription,
          DepanFxNodeInfoProperty.PropertyKind.POS, true);
    }

    private static final DepanFxNodeInfoProperty[] PROPERTIES =
        new DepanFxNodeInfoProperty[] {
            buildPosProperty("X Pos", "X position of the node"),
            buildPosProperty("Y Pos", "Y position of the node"),
            buildPosProperty("Z Pos", "Z position of the node")
    };

    public NodePositionInfoContribution() {
      super(
          DepanFxNodeLocationData.class.getName(),
          NODE_POSITION_LABEL,
          NODE_POSITION_DESCR,
          DepanFxNodeLocationData.class,
          NODE_POSITION_KEY,
          Arrays.asList(PROPERTIES));
    }
  }

}
