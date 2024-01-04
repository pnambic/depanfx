package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData.ContainerOrder;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxAnalysisExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectResource;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

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
public class DepanFxNodeListSectionConfiguration {

  private static final String OPEN_AS_LIST = "Open as ListView";

  private final DepanFxBuiltInContribution memberFinderlinkMatcherContrib =
      buildMemberFinderLinkMatcher();

  @Autowired
  public DepanFxNodeListSectionConfiguration() {
  }

  @Bean
  public DepanFxBuiltInContribution memberTreeSection() {
    DepanFxTreeSectionData toolData = buildInitialTreeSection(
        "Member Tree Section",
        "Tree section based on a link matcher for membership");
    return createBuiltIn("Member Tree", toolData);
  }

  @Bean
  public DepanFxBuiltInContribution flatSection() {
    DepanFxFlatSectionData toolData =
        new DepanFxFlatSectionData(
            "Built-in Flat Section", "Built-in flat section.",
            DepanFxFlatSectionData.BASE_SECTION_LABEL, true,
            OrderBy.NODE_KEY, OrderDirection.FORWARD);
    return new DepanFxBuiltInContribution.Simple(
        DepanFxNodeListSectionData.SIMPLE_SECTION_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution memberFinderLinkMatcher() {
    return memberFinderlinkMatcherContrib;
  }

  @Bean
  public DepanFxResourceExtMenuContribution flatSectionExtMenu() {
    return new FlatSectionExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution flatSectionPathMenu() {
    return new FlatSectionPathContribution();
  }

  @Bean
  public DepanFxResourceExtMenuContribution treeSectionExtMenu() {
    return new TreeSectionExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution treeSectionPathMenu() {
    return new TreeSectionPathContribution(
        memberFinderlinkMatcherContrib.getPath());
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution graphAsListExtMenu() {
    return new GraphAsListContribution();
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution nodeListExtMenu() {
    return new NodeListContribution();
  }

  public DepanFxTreeSectionData buildInitialTreeSection(
      String toolName, String toolDescription) {
    return new DepanFxTreeSectionData(
        toolName, toolDescription,
        "Tree", true,
        new DepanFxProjectResource.BuiltIn(
            memberFinderlinkMatcherContrib.getPath()), true,
        OrderBy.NODE_LEAF, ContainerOrder.LAST, OrderDirection.FORWARD);
  }

  private DepanFxBuiltInContribution buildMemberFinderLinkMatcher() {
    Path docPath = DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
        .resolve("Tree Member");
    DepanFxLinkMatcherDocument finderMatcher =
        new DepanFxLinkMatcherDocument(
            null, DepanFxLinkMatcherGroup.MEMBER_MATCHER_GROUP, null);
    return new DepanFxBuiltInContribution.Simple(docPath, finderMatcher);
  }

  private DepanFxBuiltInContribution createBuiltIn(
      String docName, Object doc) {
    Path docPath =
        DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.resolve(docName);
    return new DepanFxBuiltInContribution.Simple(docPath, doc);
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
        Optional<DepanFxWorkspaceResource> optWkspRsrc =
            workspace.toProjectDocument(docPath.toUri())
                .flatMap(r -> workspace.getWorkspaceResource(
                      r, GraphDocument.class));
        optWkspRsrc.map(DepanFxNodeLists::buildNodeList)
            .ifPresent(nl -> {
              String title = DepanFxWorkspaceFactory.buildDocTitle(
                  optWkspRsrc.get().getDocument()) + " nodes";
              addNodeListViewToScene(scene, dialogRunner, workspace, nl, title);
            });
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open list view for {}",
            docPath.toUri(), errCaught);
      }
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
        Optional<DepanFxWorkspaceResource> optWkspRsrc =
            workspace.toProjectDocument(docPath.toUri())
                .flatMap(r -> workspace.getWorkspaceResource(
                      r, DepanFxNodeList.class));
        optWkspRsrc.map(r -> r.getResource())
            .map(DepanFxNodeList.class::cast)
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
  }

  private static void addNodeListViewToScene(
      DepanFxSceneController scene, DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      DepanFxNodeList nodeList, String tabTitle) {
    List<DepanFxNodeListSection> sections = new ArrayList<>();
    DepanFxNodeListSectionData.getBuiltinSimpleSectionResource(workspace)
        .ifPresent(r -> sections.add(new DepanFxFlatSection(r)));

    DepanFxNodeListViewer viewer =
        new DepanFxNodeListViewer(
            workspace, dialogRunner, nodeList, sections);

    viewer.prependMemberTree();
    Tab viewerTab = viewer.createWorkspaceTab(tabTitle);
    scene.addTab(viewerTab);
  }

  private static class FlatSectionExtContribution
      extends DepanFxResourceExtMenuContribution.Basic {

    public FlatSectionExtContribution() {
      super(DepanFxFlatSectionData.class,
          DepanFxFlatSection.EDIT_FLAT_SECTION_DATA,
          DepanFxFlatSectionData.FLAT_SECTION_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource wkspRsrc, DepanFxDialogRunner dialogRunner) {
      DepanFxFlatSectionToolDialog.runEditDialog(
          wkspRsrc.getDocument(),
          (DepanFxFlatSectionData) wkspRsrc.getResource(),
          dialogRunner);
    }
  }

  private static class FlatSectionPathContribution
      implements DepanFxResourcePathMenuContribution {

    private static final String NEW_FLAT_SECTION_NAME = "New Flat Section";

    private static final String NEW_FLAT_SECTION_DESCR = "New flat section.";

    private static Logger LOG =
        LoggerFactory.getLogger(TreeSectionPathContribution.class);

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(
          DepanFxFlatSection.NEW_FLAT_SECTION_DATA,
          e -> runNewFlatSectionDataAction(dialogRunner));
    }

    private void runNewFlatSectionDataAction(
        DepanFxDialogRunner dialogRunner) {
      try {
        DepanFxFlatSectionData sectionData =
            new DepanFxFlatSectionData(
                NEW_FLAT_SECTION_NAME, NEW_FLAT_SECTION_DESCR,
                DepanFxFlatSectionData.BASE_SECTION_LABEL, true,
                OrderBy.NODE_KEY, OrderDirection.FORWARD);

        DepanFxFlatSectionToolDialog.runCreateDialog(sectionData, dialogRunner);
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to create flat section data", errCaught);
      }
    }
  }

  private static class TreeSectionExtContribution
      extends DepanFxResourceExtMenuContribution.Basic {

    public TreeSectionExtContribution() {
      super(DepanFxTreeSectionData.class,
          DepanFxTreeSection.EDIT_TREE_SECTION_DATA,
          DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource wkspRsrc, DepanFxDialogRunner dialogRunner) {
      DepanFxTreeSectionToolDialog.runEditDialog(
          wkspRsrc.getDocument(),
          (DepanFxTreeSectionData) wkspRsrc.getResource(),
          dialogRunner);
    }
  }

  private static class TreeSectionPathContribution
      implements DepanFxResourcePathMenuContribution {

    private static final String NEW_TREE_SECTION_NAME = "New Tree Section";

    private static final String NEW_TREE_SECTION_DESCR = "New tree section.";

    private static final String NEW_TREE_SECTION_LABEL = "Tree";

    private static Logger LOG =
        LoggerFactory.getLogger(TreeSectionPathContribution.class);

    private final Path builtInMatcherPath;

    public TreeSectionPathContribution(Path builtInMatcherPath) {
      this.builtInMatcherPath = builtInMatcherPath;
    }

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(
          DepanFxTreeSection.NEW_TREE_SECTION_DATA,
          e -> runNewTreeSectionDataAction(dialogRunner));
    }

    private void runNewTreeSectionDataAction(
        DepanFxDialogRunner dialogRunner) {
      try {
        DepanFxTreeSectionData sectionData = buildInitialTreeSection();
        DepanFxTreeSectionToolDialog.runCreateDialog(sectionData, dialogRunner);
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to create tree section data", errCaught);
      }
    }

    private DepanFxTreeSectionData buildInitialTreeSection() {
      return new DepanFxTreeSectionData(
          NEW_TREE_SECTION_NAME, NEW_TREE_SECTION_DESCR,
          NEW_TREE_SECTION_LABEL, true,
          new DepanFxProjectResource.BuiltIn(builtInMatcherPath), true,
          OrderBy.NODE_LEAF, ContainerOrder.LAST, OrderDirection.FORWARD);
    }
  }
}
