package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData.ContainerOrder;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeListSectionConfiguration {

  private static final String FLAT_SECTION_KEY = "Flat Section";

  private static final String TREE_SECTION_KEY = "Tree Section";

  public static final Path MEMBER_MATCHER_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH.resolve("Tree Member");

  public static final Path MEMBER_TREE_SECTION_PATH =
      DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.resolve("Member Tree");

  @Autowired
  public DepanFxNodeListSectionConfiguration() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeSectionData>
      memberTreeSection() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxTreeSectionData>(
        MEMBER_TREE_SECTION_PATH) {

      @Override
      protected DepanFxTreeSectionData buildDocument(
          DepanFxBuiltInProject project) {
        return buildMemberTreeSection(
            project, "Member Tree Section",
            "Tree section based on a link matcher for membership");
      }
    };
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxFlatSectionData> flatSection() {
    DepanFxFlatSectionData toolData =
        new DepanFxFlatSectionData(
            "Built-in Flat Section", "Built-in flat section.",
            DepanFxFlatSectionData.BASE_SECTION_LABEL, true,
            OrderBy.NODE_KEY, OrderDirection.FORWARD);
    return new DepanFxBuiltInContribution.Simple<>(
        DepanFxNodeListSectionData.SIMPLE_SECTION_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      memberFinderLinkMatcher() {

    DepanFxLinkMatcherDocument finderMatcher =
        new DepanFxLinkMatcherDocument(
            "Tree Member", "Synthetic tree membership",
            null, DepanFxLinkMatcherGroup.MEMBER_MATCHER_GROUP, null);
    return new DepanFxBuiltInContribution.Simple<>(
        MEMBER_MATCHER_PATH, finderMatcher);
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
    return new TreeSectionPathContribution();
  }

  private DepanFxTreeSectionData buildMemberTreeSection(
      DepanFxBuiltInProject project, String toolName, String toolDescription) {

    Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
        optMatcherRsrc = project.getResource(MEMBER_MATCHER_PATH);
    DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc =
        optMatcherRsrc.orElseThrow(() ->
            new DepanFxBuiltInContribution.MissingDependencyException(
                MEMBER_TREE_SECTION_PATH, MEMBER_MATCHER_PATH));

    return new DepanFxTreeSectionData(
        toolName, toolDescription, "Tree", true, matcherRsrc, true,
        OrderBy.NODE_LEAF, ContainerOrder.LAST, OrderDirection.FORWARD);
  }

  /////////////////////////////////////
  // Flat section contributions

  private static class FlatSectionExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxFlatSectionData> {

    public FlatSectionExtContribution() {
      super(DepanFxFlatSectionData.class, FLAT_SECTION_KEY,
          DepanFxFlatSection.EDIT_FLAT_SECTION_DATA,
          DepanFxFlatSectionData.FLAT_SECTION_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxFlatSectionData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxFlatSectionToolDialog.runEditDialog(
          wkspRsrc.getDocument(), wkspRsrc.getResource(), dialogRunner);
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
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
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

    @Override
    public String getOrderKey() {
      return FLAT_SECTION_KEY;
    }
  }

  /////////////////////////////////////
  // Tree section GUI contributions

  private static class TreeSectionExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxTreeSectionData> {

    public TreeSectionExtContribution() {
      super(DepanFxTreeSectionData.class, TREE_SECTION_KEY,
          DepanFxTreeSection.EDIT_TREE_SECTION_DATA,
          DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxTreeSectionData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxTreeSectionToolDialog.runEditDialog(
          wkspRsrc.getDocument(), wkspRsrc.getResource(), dialogRunner);
    }
  }

  private static class TreeSectionPathContribution
      implements DepanFxResourcePathMenuContribution {

    private static final String NEW_TREE_SECTION_NAME = "New Tree Section";

    private static final String NEW_TREE_SECTION_DESCR = "New tree section.";

    private static final String NEW_TREE_SECTION_LABEL = "Tree";

    private static Logger LOG =
        LoggerFactory.getLogger(TreeSectionPathContribution.class);

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(
          DepanFxTreeSection.NEW_TREE_SECTION_DATA,
          e -> runNewTreeSectionDataAction(dialogRunner, workspace));
    }

    private void runNewTreeSectionDataAction(
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace) {
      try {
        DepanFxTreeSectionData sectionData = buildInitialTreeSection(workspace);
        DepanFxTreeSectionToolDialog.runCreateDialog(sectionData, dialogRunner);
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to create tree section data", errCaught);
      }
    }

    private DepanFxTreeSectionData buildInitialTreeSection(
        DepanFxWorkspace workspace) {
      Optional<DepanFxProjectDocument> matcherProjPath =
          workspace.getBuiltInProjectTree().asProjectDocument(MEMBER_MATCHER_PATH);
      return  workspace.getWorkspaceResource(
          matcherProjPath.get(), DepanFxLinkMatcherDocument.class)
          .map(m -> new DepanFxTreeSectionData(
                NEW_TREE_SECTION_NAME, NEW_TREE_SECTION_DESCR,
                NEW_TREE_SECTION_LABEL, true, m, true,
                OrderBy.NODE_LEAF, ContainerOrder.LAST, OrderDirection.FORWARD))
          .get();
    }

    @Override
    public String getOrderKey() {
      return TREE_SECTION_KEY;
    }
  }
}
