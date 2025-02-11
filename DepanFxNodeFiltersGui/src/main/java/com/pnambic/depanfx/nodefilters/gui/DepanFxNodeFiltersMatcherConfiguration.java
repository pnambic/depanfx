package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxMatcherFilterData;
import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceOpenRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.TreeItem;

@Configuration
public class DepanFxNodeFiltersMatcherConfiguration {

  public static final String ADD_MATCHER_FILTER = "Add Link Matcher Filter...";

  public static final String EDIT_LINK_MATCHER_FILTER =
      "Edit Link Matcher Filter...";

  public static final String NEW_LINK_MATCHER_FILTER = "New Link Matcher Filter...";

  private static final String LINK_MATCHER_KEY = "Link Matcher";

  @Bean
  public DepanFxResourceOpenRegistry.Contribution
      linkMatcherFilterFileOpenMenu() {

    return new LinkMatcherFilterFileOpenContribution();
  }

  @Bean
  public DepanFxResourceExtMenuContribution linkMatcherFilterExtMenu() {
    return new LinkMatcherFilterExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution linkMatcherFilterPathMenu() {
    return new LinkMatcherFilterPathContribution();
  }

  @Bean
  public DepanFxNodeFiltersDialogContribution nodeFilterMatcherContribution(
      DepanFxWorkspace workspace) {
    return new DepanFxNodeFiltersMatcherContribution(workspace);
  }

  private static class LinkMatcherFilterFileOpenContribution
      extends DepanFxResourceOpenRegistry.Basic<DepanFxMatcherFilterData> {

    public LinkMatcherFilterFileOpenContribution() {
      super(
          DepanFxMatcherFilterData.class,
          DepanFxMatcherFilterData.MATCHER_FILTER_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxMatcherFilterData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeFiltersMatcherDialog.runEditDialog(dialogRunner, wkspRsrc);
    }
  }

  private static class LinkMatcherFilterExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxMatcherFilterData> {

    public LinkMatcherFilterExtContribution() {
      super(DepanFxMatcherFilterData.class,
          LINK_MATCHER_KEY, EDIT_LINK_MATCHER_FILTER,
          DepanFxMatcherFilterData.MATCHER_FILTER_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxMatcherFilterData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeFiltersMatcherDialog.runEditDialog(dialogRunner, wkspRsrc);
    }
  }

  private static class LinkMatcherFilterPathContribution
      implements DepanFxResourcePathMenuContribution {

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxBaseFilterData.NODE_FILTERS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(NEW_LINK_MATCHER_FILTER,
          e -> runCreateNodeListFilter(workspace, dialogRunner));
    }

    private void runCreateNodeListFilter(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      DepanFxMatcherFilterData filterData =
          DepanFxMatcherFilterData.createMatcherFilterData(null);
      DepanFxNodeFiltersMatcherDialog.runSaveFilter(
          dialogRunner, workspace.addScratchResource(filterData));
    }

    @Override
    public String getOrderKey() {
      return LINK_MATCHER_KEY;
    }
  }

  private static class DepanFxNodeFiltersMatcherContribution
      extends DepanFxNodeFiltersDialogContribution.Basic<DepanFxMatcherFilterData> {

    private final DepanFxWorkspace workspace;

    public DepanFxNodeFiltersMatcherContribution(DepanFxWorkspace workspace) {
      super(LINK_MATCHER_KEY, ADD_MATCHER_FILTER,
          DepanFxMatcherFilterData.class);
      this.workspace = workspace;
    }

    @Override
    public TreeItem<DepanFxNodeFiltersTableMember> buildTableMember(
        DepanFxNodeFiltersTableMember parentMember,
        DepanFxBaseFilterData filter,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return new DepanFxNodeFiltersMatcherItem(
          new DepanFxNodeFiltersMatcherMember(parentMember, asType(filter)));
    }

    @Override
    public void runSaveFilter(DepanFxDialogRunner dialogRunner,
        DepanFxBaseFilterData saveFilter) {
      DepanFxWorkspaceResource<DepanFxMatcherFilterData> saveRsrc =
          workspace.addScratchResource(asType(saveFilter));
      DepanFxNodeFiltersMatcherDialog.runSaveFilter(
          dialogRunner, saveRsrc);
    }

    @Override
    public Optional<DepanFxMatcherFilterData> runUpdateFilter(
        DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData updateFilter) {
      return DepanFxNodeFiltersMatcherDialog.runUpdateFilter(
          workspace, dialogRunner, asType(updateFilter));
    }

    @Override
    protected Optional<DepanFxMatcherFilterData> runCreateFilter(
        ActionEvent e,
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return DepanFxLinkMatcherChooser.runLinkMatcherFinder(
                workspace, dialogRunner, scene)
            .map(DepanFxMatcherFilterData::createMatcherFilterData);
    }
  }
}
