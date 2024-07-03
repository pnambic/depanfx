package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxListFilterData;
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
public class DepanFxNodeFiltersListConfiguration {

  public static final String EDIT_NODE_LIST_FILTER = "Edit Node List Filter...";

  public static final String NEW_NODE_LIST_FILTER = "New Node List Filter...";

  private static final String NODE_LIST_KEY = "Node List";

  @Bean
  public DepanFxResourceExtMenuContribution nodeListFilterExtMenu() {
    return new NodeListFilterExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution nodeListFilterPathMenu() {
    return new NodeListFilterPathContribution();
  }

  private static class NodeListFilterExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxListFilterData> {

    public NodeListFilterExtContribution() {
      super(DepanFxListFilterData.class, NODE_LIST_KEY, EDIT_NODE_LIST_FILTER,
          DepanFxListFilterData.LIST_FILTER_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxListFilterData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeFiltersListDialog.runEditDialog(dialogRunner, wkspRsrc);
    }
  }

  private static class NodeListFilterPathContribution
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
      builder.appendActionItem(NEW_NODE_LIST_FILTER,
          e -> runCreateNodeListFilter(dialogRunner));
    }

    private void runCreateNodeListFilter(DepanFxDialogRunner dialogRunner) {
      DepanFxListFilterData filterData =
          DepanFxListFilterData.createListFilterData(null);
      DepanFxNodeFiltersListDialog.runSaveFilter(dialogRunner, filterData);
    }

    @Override
    public String getOrderKey() {
      return NODE_LIST_KEY;
    }
  }
}
