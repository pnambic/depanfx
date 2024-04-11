package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxAnalysisExtMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeViewConfiguration {

  public static final String NODE_VIEW_KEY = "Node View";

  private static final String OPEN_VIEW = "Open Node View";

  private static final String OPEN_AS_VIEW = "Open as Node View";

  private final DepanFxNodeLayoutRegistry layoutRegistry;

  @Autowired
  public DepanFxNodeViewConfiguration(
      DepanFxNodeLayoutRegistry layoutRegistry) {
    this.layoutRegistry = layoutRegistry;
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution graphNodeViewExtMenu() {
    return new GraphContribution(layoutRegistry);
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution listNodeViewExtMenu() {
    return new NodeListContribution(layoutRegistry);
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution nodeViewExtMenu() {
    return new NodeViewContribution(layoutRegistry);
  }

  private static abstract class BaseNodeViewExtMenuContribution<T>
      implements DepanFxAnalysisExtMenuContribution {

    private static final Logger LOG =
        LoggerFactory.getLogger(BaseNodeViewExtMenuContribution.class);

    private final Class<T> resourceType;

    private final String menuLabel;

    private final String viewExt;

    private final DepanFxNodeLayoutRegistry layoutRegistry;

    public BaseNodeViewExtMenuContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        Class<T> resourceType, String menuLabel, String viewExt) {
      this.layoutRegistry = layoutRegistry;
      this.resourceType = resourceType;
      this.menuLabel = menuLabel;
      this.viewExt = viewExt;
    }

    abstract protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace, DepanFxWorkspaceResource<?> rsrc);

    @Override
    public boolean acceptsExt(String ext) {
      return viewExt.equals(ext);
    }

    @Override
    public void prepareCell(
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell, String ext,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      Path docPath = member.getMemberPath();

      installOnOpen(scene, dialogRunner, workspace, cell, docPath);
      builder.appendActionItem(
          menuLabel,
          e -> runOpenNodeListAction(scene, dialogRunner, workspace, docPath));
    }

    protected void installOnOpen(
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell, Path docPath) {
      // The default is no onOpen action.
    }


    protected void runOpenNodeListAction(
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace,
        Path docPath) {

      try {
        Optional<DepanFxWorkspaceResource<T>> optWkspRsrc =
            workspace.toProjectDocument(docPath.toUri())
                .flatMap(r ->
                    workspace.getWorkspaceResource(r, resourceType));
        optWkspRsrc
            .map(r -> getNodeViewData(workspace, r))
            .ifPresent(nv ->
                addNodeViewPanelToScene(scene, dialogRunner, workspace, nv));
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open node view for {}",
            docPath.toUri(), errCaught);
      }
    }

    private void addNodeViewPanelToScene(DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        DepanFxNodeViewData viewData) {
      DepanFxNodeViewPanel viewPanel = new DepanFxNodeViewPanel(
          workspace, dialogRunner, layoutRegistry, viewData);
      scene.addTab(viewPanel.createWorkspaceTab(viewData.getToolName()));
    }
  }

  private class GraphContribution
      extends BaseNodeViewExtMenuContribution<GraphDocument> {

    public GraphContribution(DepanFxNodeLayoutRegistry layoutRegistry) {
      super(
          layoutRegistry,
          GraphDocument.class, OPEN_AS_VIEW,
          GraphDocPersistenceContribution.EXTENSION);
    }

    @Override
    protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<?> rsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<GraphDocument> graphDocResource =
          (DepanFxWorkspaceResource<GraphDocument>) rsrc;

      GraphDocument graphDoc = graphDocResource.getResource();
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> linkDisplayDocRsrc =
          getContextLinkDisplay(workspace, graphDoc.getContextModelId());

      return DepanFxNodeViews.fromGraphDocument(
          graphDocResource, linkDisplayDocRsrc);
    }

    @Override
    public String getOrderKey() {
      return NODE_VIEW_KEY;
    }
  }

  private class NodeListContribution
      extends BaseNodeViewExtMenuContribution<DepanFxNodeList> {

    public NodeListContribution(DepanFxNodeLayoutRegistry layoutRegistry) {
      super(
          layoutRegistry,
          DepanFxNodeList.class, OPEN_AS_VIEW,
          DepanFxNodeList.NODE_LIST_EXT);
    }

    @Override
    protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<?> rsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListResource =
          (DepanFxWorkspaceResource<DepanFxNodeList>) rsrc;

      DepanFxNodeList nodeList = nodeListResource.getResource();
      GraphDocument graphDoc =
          (GraphDocument) nodeList.getGraphDocResource().getResource();
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>
          linkDisplayDocRsrc =
              getContextLinkDisplay(workspace, graphDoc.getContextModelId());

      return DepanFxNodeViews.fromNodeList(
          nodeListResource, linkDisplayDocRsrc);
    }

    @Override
    public String getOrderKey() {
      return NODE_VIEW_KEY;
    }
  }

  private static class NodeViewContribution
      extends BaseNodeViewExtMenuContribution<DepanFxNodeViewData> {

    public NodeViewContribution(DepanFxNodeLayoutRegistry layoutRegistry) {
      super(
          layoutRegistry,
          DepanFxNodeViewData.class, OPEN_VIEW,
          DepanFxNodeViewData.NODE_VIEW_TOOL_EXT);
    }

    @Override
    protected void installOnOpen(
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell, Path docPath) {
      DepanFxResourcePerspectives.installOnOpen(cell, docPath,
          p -> runOpenNodeListAction(scene, dialogRunner, workspace, p));
    }

    @Override
    protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<?> rsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<DepanFxNodeViewData> nodeViewResource =
          (DepanFxWorkspaceResource<DepanFxNodeViewData>) rsrc;

      return nodeViewResource.getResource();
    }

    @Override
    public String getOrderKey() {
      return NODE_VIEW_KEY;
    }
  }

  private DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>
      getContextLinkDisplay(
            DepanFxWorkspace workspace, ContextModelId contextModelId) {

    return DepanFxProjects.getBuiltIn(
            workspace, DepanFxNodeViewLinkDisplayData.class,
            c -> byContextModel(c, contextModelId))
        .orElseGet(() ->
            DepanFxProjects.getBuiltIn(
                workspace, DepanFxNodeViewLinkDisplayData.class,
                DepanFxNodeViewLinkDisplayDataBuiltIns.ALL_EDGES_DOC_PATH)
            .get());
  }

  private boolean byContextModel(
      DepanFxBuiltInContribution<DepanFxNodeViewLinkDisplayData> viewDisplayContrib,
      ContextModelId contextModelId) {
    return viewDisplayContrib.getDocument()
        .getContextModelId().equals(contextModelId);
  }
}
