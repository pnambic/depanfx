package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxMatcherFilterData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
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

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeFiltersMatcherConfiguration {

  public static final String EDIT_LINK_MATCHER_FILTER =
      "Edit Link Matcher Filter...";

  public static final String NEW_LINK_MATCHER_FILTER = "New Link Matcher Filter...";

  private static final String LINK_MATCHER_KEY = "Link Matcher";

  @Bean
  public DepanFxResourceExtMenuContribution linkMatcherFilterExtMenu() {
    return new LinkMatcherFilterExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution linkMatcherFilterPathMenu() {
    return new LinkMatcherFilterPathContribution();
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
          e -> runCreateNodeListFilter(dialogRunner));
    }

    private void runCreateNodeListFilter(DepanFxDialogRunner dialogRunner) {
      DepanFxMatcherFilterData filterData =
          DepanFxMatcherFilterData.createMatcherFilterData(null);
      DepanFxNodeFiltersMatcherDialog.runSaveFilter(dialogRunner, filterData);
    }

    @Override
    public String getOrderKey() {
      return LINK_MATCHER_KEY;
    }
  }
}
