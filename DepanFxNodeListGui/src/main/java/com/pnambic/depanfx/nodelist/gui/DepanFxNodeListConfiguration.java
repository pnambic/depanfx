package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumnConfiguration;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionConfiguration;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
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
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;
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

  public static final Path FLAT_TABLE_VIEW_PATH =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
          .resolve("Flat Table View");

  public static final Path MEMBER_TABLE_VIEW_PATH =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
          .resolve("Member Table View");

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

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      flatTableView() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData>(
        FLAT_TABLE_VIEW_PATH) {

      @Override
      protected DepanFxNodeListTableViewData buildDocument(
          DepanFxBuiltInProject project) {

        // Flat Section
        List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
            sectionRsrcs = new ArrayList<>();
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionData.SIMPLE_SECTION_TOOL_PATH));

        // Node Kind Column
        List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
            columnRsrcs = new ArrayList<>();
        columnRsrcs.add(getResource(project,
            DepanFxNodeKeyColumnConfiguration.KIND_KEY_COLUMN_TOOL_PATH));

        return new DepanFxNodeListTableViewData(
            "Flat Table View", "Flat node list table view",
            sectionRsrcs, columnRsrcs);
      }
    };
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      memberTableView() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData>(
        MEMBER_TABLE_VIEW_PATH) {

      @Override
      protected DepanFxNodeListTableViewData buildDocument(
          DepanFxBuiltInProject project) {

        // Flat and Members Sections
        List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
            sectionRsrcs = new ArrayList<>();
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionConfiguration.MEMBER_TREE_SECTION_PATH));
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionData.SIMPLE_SECTION_TOOL_PATH));

        // Node Kind Column
        List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
            columnRsrcs = new ArrayList<>();
        columnRsrcs.add(getResource(project,
            DepanFxNodeKeyColumnConfiguration.KIND_KEY_COLUMN_TOOL_PATH));

        return new DepanFxNodeListTableViewData(
            "Member Table View", "Table view membership",
            sectionRsrcs, columnRsrcs);
      }
    };
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
        Optional<DepanFxWorkspaceResource<GraphDocument>> optWkspRsrc =
            workspace.toProjectDocument(docPath.toUri())
                .flatMap(r ->
                    workspace.getWorkspaceResource(r, GraphDocument.class));
        optWkspRsrc.map(DepanFxNodeLists::buildNodeList)
            .ifPresent(nl -> {
              String title = DepanFxWorkspaceFactory.buildDocTitle(
                  optWkspRsrc.get().getDocument()) + " nodes";
              addNodeListViewToScene(workspace, dialogRunner, scene, nl, title);
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
        Optional<DepanFxWorkspaceResource<DepanFxNodeList>> optWkspRsrc =
            workspace.toProjectDocument(docPath.toUri())
                .flatMap(r -> workspace.getWorkspaceResource(
                      r, DepanFxNodeList.class));
        optWkspRsrc.map(r -> r.getResource())
            .ifPresent(nl -> {
              String title = DepanFxWorkspaceFactory.buildDocTitle(
                  optWkspRsrc.get().getDocument());
              addNodeListViewToScene(workspace, dialogRunner, scene, nl, title);
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
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxSceneController scene,
      DepanFxNodeList nodeList, String tabTitle) {

    DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc =
        DepanFxProjects.getBuiltIn(
            workspace,  DepanFxNodeListTableViewData.class,
            DepanFxNodeListConfiguration.MEMBER_TABLE_VIEW_PATH).get();

    DepanFxNodeListViewer viewer = new DepanFxNodeListViewer(
        workspace, dialogRunner, nodeList, tableViewRsrc.getResource());

    Tab viewerTab = viewer.createWorkspaceTab(tabTitle);
    scene.addTab(viewerTab);
  }
}
