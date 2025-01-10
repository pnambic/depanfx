package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxAnalysisExtMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeViewConfiguration {

  public static final String NODE_VIEW_KEY = "Node View";

  private static final String OPEN_VIEW = "Open Node View";

  private static final String OPEN_AS_VIEW = "Open as Node View";

  private final DepanFxNodeLayoutRegistry layoutRegistry;

  private final DepanFxNodeFiltersRegistry filterRegistry;

  @Autowired
  public DepanFxNodeViewConfiguration(
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry) {
    this.layoutRegistry = layoutRegistry;
    this.filterRegistry = filterRegistry;
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution graphNodeViewExtMenu() {
    return new GraphContribution(layoutRegistry, filterRegistry);
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution listNodeViewExtMenu() {
    return new NodeListContribution(layoutRegistry, filterRegistry);
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution nodeViewExtMenu() {
    return new NodeViewContribution(layoutRegistry, filterRegistry);
  }

  private static abstract class BaseNodeViewExtMenuContribution<T>
      implements DepanFxAnalysisExtMenuContribution {

    private static final Logger LOG =
        LoggerFactory.getLogger(BaseNodeViewExtMenuContribution.class);

    private final Class<T> resourceType;

    private final String menuLabel;

    private final String viewExt;

    private final DepanFxNodeLayoutRegistry layoutRegistry;

    private final DepanFxNodeFiltersRegistry filterRegistry;

    public BaseNodeViewExtMenuContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry,
        Class<T> resourceType, String menuLabel, String viewExt) {
      this.layoutRegistry = layoutRegistry;
      this.filterRegistry = filterRegistry;
      this.resourceType = resourceType;
      this.menuLabel = menuLabel;
      this.viewExt = viewExt;
    }

    abstract protected DepanFxWorkspaceResource<DepanFxNodeViewData> getNodeViewRsrc(
        DepanFxWorkspace workspace, DepanFxWorkspaceResource<?> rsrc);

    // abstract protected DepanFxWorkspaceResource<DepanFxNodeViewData> XgetNodeViewData(
    //    DepanFxWorkspace workspace, DepanFxWorkspaceResource<?> rsrc);

    @Override
    public boolean acceptsExt(String ext) {
      return viewExt.equals(ext);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner, DepanFxSceneController scene,
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
        workspace.toProjectDocument(docPath.toUri())
            .flatMap(r ->
                workspace.getWorkspaceResource(r, resourceType))
            .map(r -> getNodeViewRsrc(workspace, r))
            .ifPresent(r ->
                addNodeViewPanelToScene(scene, dialogRunner, workspace, r));
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open node view for {}",
            docPath.toUri(), errCaught);
      }
    }

    private void addNodeViewPanelToScene(DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<DepanFxNodeViewData> nodeViewRsrc) {
      DepanFxNodeViewPanel viewPanel = new DepanFxNodeViewPanel(
          workspace, dialogRunner, layoutRegistry, filterRegistry, nodeViewRsrc);
      scene.addViewer(viewPanel);
    }
  }

  private class GraphContribution
      extends BaseNodeViewExtMenuContribution<GraphDocument> {

    public GraphContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry) {
      super(
          layoutRegistry, filterRegistry,
          GraphDocument.class, OPEN_AS_VIEW,
          GraphDocPersistenceContribution.EXTENSION);
    }

    @Override
    public String getOrderKey() {
      return NODE_VIEW_KEY;
    }

    @Override
    protected DepanFxWorkspaceResource<DepanFxNodeViewData> getNodeViewRsrc(
        DepanFxWorkspace workspace, DepanFxWorkspaceResource<?> rsrc) {
      return workspace.addScratchResource(getNodeViewData(workspace, rsrc));
    }

    private DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<?> rsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<GraphDocument> graphDocResource =
          (DepanFxWorkspaceResource<GraphDocument>) rsrc;

      return DepanFxNodeViews.fromGraphDocument(
          graphDocResource, workspace, layoutRegistry);
    }
  }

  private class NodeListContribution
      extends BaseNodeViewExtMenuContribution<DepanFxNodeList> {

    public NodeListContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry) {
      super(
          layoutRegistry, filterRegistry,
          DepanFxNodeList.class, OPEN_AS_VIEW,
          DepanFxNodeList.NODE_LIST_EXT);
    }

    @Override
    public String getOrderKey() {
      return NODE_VIEW_KEY;
    }

    @Override
    protected DepanFxWorkspaceResource<DepanFxNodeViewData> getNodeViewRsrc(
        DepanFxWorkspace workspace, DepanFxWorkspaceResource<?> rsrc) {
      return workspace.addScratchResource(getNodeViewData(workspace, rsrc));
    }

    private DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource<?> rsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListResource =
          (DepanFxWorkspaceResource<DepanFxNodeList>) rsrc;

      return DepanFxNodeViews.fromNodeList(
          nodeListResource, workspace, layoutRegistry);
    }
  }

  private static class NodeViewContribution
      extends BaseNodeViewExtMenuContribution<DepanFxNodeViewData> {

    public NodeViewContribution(
        DepanFxNodeLayoutRegistry layoutRegistry,
        DepanFxNodeFiltersRegistry filterRegistry) {
      super(
          layoutRegistry, filterRegistry,
          DepanFxNodeViewData.class, OPEN_VIEW,
          DepanFxNodeViewData.NODE_VIEW_TOOL_EXT);
    }

    @Override
    public String getOrderKey() {
      return NODE_VIEW_KEY;
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
    @SuppressWarnings("unchecked")
    protected DepanFxWorkspaceResource<DepanFxNodeViewData> getNodeViewRsrc(
        DepanFxWorkspace workspace, DepanFxWorkspaceResource<?> rsrc) {
      return (DepanFxWorkspaceResource<DepanFxNodeViewData>) rsrc;
    }
  }
}
