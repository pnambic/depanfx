package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.persistence.GraphDocPersistenceContribution;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
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

  private static final String OPEN_VIEW = "Open Node View";

  private static final String OPEN_AS_VIEW = "Open as Node View";

  @Autowired
  public DepanFxNodeViewConfiguration() {
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution graphNodeViewExtMenu() {
    return new GraphContribution();
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution listNodeViewExtMenu() {
    return new NodeListContribution();
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution nodeViewExtMenu() {
    return new NodeViewContribution();
  }

  private static abstract class BaseNodeViewExtMenuContribution
      implements DepanFxAnalysisExtMenuContribution {

    private static final Logger LOG =
        LoggerFactory.getLogger(BaseNodeViewExtMenuContribution.class);

    private final Class<?> resourceType;

    private final String menuLabel;

    private final String viewExt;

    public BaseNodeViewExtMenuContribution(
        Class<?> resourceType, String menuLabel, String viewExt) {
      this.resourceType = resourceType;
      this.menuLabel = menuLabel;
      this.viewExt = viewExt;
    }

    abstract protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace, DepanFxWorkspaceResource rsrc);

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
        Optional<DepanFxWorkspaceResource> optWkspRsrc =
            workspace.toProjectDocument(docPath.toUri())
                .flatMap(r -> workspace.getWorkspaceResource(
                      r, resourceType));
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
      DepanFxNodeViewPanel viewPanel =
          new DepanFxNodeViewPanel(workspace, dialogRunner, viewData);
      scene.addTab(viewPanel.createWorkspaceTab(viewData.getToolName()));
    }
  }

  private class GraphContribution
      extends BaseNodeViewExtMenuContribution {

    public GraphContribution() {
      super(GraphDocument.class, OPEN_AS_VIEW,
          GraphDocPersistenceContribution.EXTENSION);
    }

    @Override
    protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource rsrc) {
      GraphDocument graphDoc = (GraphDocument) rsrc.getResource();
      DepanFxWorkspaceResource linkDisplayDocRsrc =
          getContextLinkDisplay(workspace, graphDoc.getContextModelId());

      return DepanFxNodeViews.fromGraphDocument(
          rsrc, linkDisplayDocRsrc);
    }
  }

  private class NodeListContribution
      extends BaseNodeViewExtMenuContribution {

    public NodeListContribution() {
      super(DepanFxNodeList.class, OPEN_AS_VIEW,
          DepanFxNodeList.NODE_LIST_EXT);
    }

    @Override
    protected DepanFxNodeViewData getNodeViewData(
        DepanFxWorkspace workspace,
        DepanFxWorkspaceResource rsrc) {
      DepanFxNodeList nodeList = (DepanFxNodeList) rsrc.getResource();
      GraphDocument graphDoc =
          (GraphDocument) nodeList.getGraphDocResource().getResource();
      DepanFxWorkspaceResource linkDisplayDocRsrc =
          getContextLinkDisplay(workspace, graphDoc.getContextModelId());

      return DepanFxNodeViews.fromNodeList(rsrc, linkDisplayDocRsrc);
    }
  }

  private static class NodeViewContribution
      extends BaseNodeViewExtMenuContribution {

    public NodeViewContribution() {
      super(DepanFxNodeViewData.class, OPEN_VIEW,
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
        DepanFxWorkspaceResource rsrc) {
      return (DepanFxNodeViewData) rsrc.getResource();
    }
  }

  private DepanFxWorkspaceResource getContextLinkDisplay(
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
      DepanFxBuiltInContribution viewDisplayContrib,
      ContextModelId contextModelId) {
    DepanFxNodeViewLinkDisplayData linkDisplayData =
        (DepanFxNodeViewLinkDisplayData) viewDisplayContrib.getDocument();
    return linkDisplayData.getContextModelId().equals(contextModelId);
  }
}
