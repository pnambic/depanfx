package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeSection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxAnalysisExtMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Cell;
import javafx.scene.control.Tab;

@Configuration
public class DepanFxNodeListConfiguration {

  private static final String NODE_LIST_KEY = "Node List Key";

  private static final String OPEN_AS_LIST = "Open as Node List";

  @Autowired
  public DepanFxNodeListConfiguration() {
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution graphAsListExtMenu() {
    return new GraphAsListContribution();
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution nodeListExtMenu() {
    return new NodeListContribution();
  }

  private static class GraphAsListContribution
      implements DepanFxAnalysisExtMenuContribution {

    private static Logger LOG =
        LoggerFactory.getLogger(NodeListContribution.class);

    @Override
    public boolean acceptsExt(String ext) {
      return "dgi".equals(ext);
    }

    @Override
    public void prepareCell(
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
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
        Optional<DepanFxWorkspaceResource<GraphDocument>> optWkspRsrc =
            workspace.toProjectDocument(docPath.toUri())
                .flatMap(r ->
                    workspace.getWorkspaceResource(r, GraphDocument.class));
        optWkspRsrc.map(DepanFxNodeLists::buildNodeList)
            .ifPresent(nl -> {
              String title = DepanFxWorkspaceFactory.buildDocTitle(
                  optWkspRsrc.get().getDocument()) + " nodes";
              addNodeListViewToScene(scene, dialogRunner, workspace, nl, title);
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
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell, String ext,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      Path docPath = member.getMemberPath();
      DepanFxResourcePerspectives.installOnOpen(cell, docPath,
          p -> runOpenNodeListAction(scene, dialogRunner, workspace, p));
      builder.appendActionItem(
          OPEN_AS_LIST,
          e -> runOpenNodeListAction(scene, dialogRunner, workspace, docPath));
    }

    private void runOpenNodeListAction(
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace,
        Path docPath) {
      try {
        Optional<DepanFxWorkspaceResource<DepanFxNodeList>> optWkspRsrc =
            workspace.toProjectDocument(docPath.toUri())
                .flatMap(r -> workspace.getWorkspaceResource(
                      r, DepanFxNodeList.class));
        optWkspRsrc.map(r -> r.getResource())
            .ifPresent(nl -> {
              String title = DepanFxWorkspaceFactory.buildDocTitle(
                  optWkspRsrc.get().getDocument());
              addNodeListViewToScene(scene, dialogRunner, workspace, nl, title);
            });
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

  private static void addNodeListViewToScene(
      DepanFxSceneController scene, DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      DepanFxNodeList nodeList, String tabTitle) {

    List<DepanFxNodeListSection> sections = new ArrayList<>();
    Optional<DepanFxFlatSection> optFlatSection =
        DepanFxNodeListSectionData.getBuiltinSimpleSectionResource(workspace)
            .map(r -> new DepanFxFlatSection(r));
    optFlatSection.ifPresent(sections::add);

    DepanFxNodeListViewer viewer =
        new DepanFxNodeListViewer(
            workspace, dialogRunner, nodeList, sections);

    ContextModelId contextModel =
        nodeList.getGraphDocResource().getResource().getContextModelId();
    DepanFxProjects.getBuiltIn(
            workspace,  DepanFxTreeSectionData.class,
            c -> byMemberLinkMatcherDoc(c, contextModel))
      .map(t -> new DepanFxTreeSection(viewer, t))
      .ifPresent(
          s -> viewer.insertSection(optFlatSection.get(), s));

    Tab viewerTab = viewer.createWorkspaceTab(tabTitle);
    scene.addTab(viewerTab);
  }

  private static boolean byMemberLinkMatcherDoc(
      DepanFxBuiltInContribution<DepanFxTreeSectionData> contrib,
      ContextModelId modelId) {
    return DepanFxLinkMatcherGroup.isContextModelMemberMatcher(
        modelId, contrib.getDocument().getLinkMatcherRsrc().getResource());
  }
}
