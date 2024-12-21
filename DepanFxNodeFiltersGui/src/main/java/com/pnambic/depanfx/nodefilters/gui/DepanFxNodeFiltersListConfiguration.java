package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxListFilterData;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
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
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.TreeItem;

@Configuration
public class DepanFxNodeFiltersListConfiguration {

  public static final String ADD_LIST_FILTER = "Add List Node Filter...";

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

  @Bean
  public DepanFxNodeFiltersDialogContribution nodeFilterListContribution(
      DepanFxWorkspace workspace) {
    return new DepanFxNodeFiltersListContribution(workspace);
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
          e -> runCreateNodeListFilter(workspace, dialogRunner));
    }

    private void runCreateNodeListFilter(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      DepanFxWorkspaceResource<DepanFxListFilterData> filterRsrc =
          workspace.addScratchResource(
              DepanFxListFilterData.createListFilterData(null));
      DepanFxNodeFiltersListDialog.runSaveFilter(
          dialogRunner, filterRsrc);
    }

    @Override
    public String getOrderKey() {
      return NODE_LIST_KEY;
    }
  }

  private static class DepanFxNodeFiltersListContribution
      extends DepanFxNodeFiltersDialogContribution.Basic<DepanFxListFilterData> {

    private DepanFxWorkspace workspace;

    public DepanFxNodeFiltersListContribution(DepanFxWorkspace workspace) {
      super(NODE_LIST_KEY, ADD_LIST_FILTER, DepanFxListFilterData.class);
      this.workspace = workspace;
    }

    @Override
    public TreeItem<DepanFxNodeFiltersTableMember> buildTableMember(
        DepanFxNodeFiltersTableMember parentMember,
        DepanFxBaseFilterData filter,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return new DepanFxNodeFiltersListItem(
          new DepanFxNodeFiltersListMember(parentMember, asType(filter)));
    }

    @Override
    public void runSaveFilter(
        DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData saveFilter) {
      DepanFxWorkspaceResource<DepanFxListFilterData> filterRsrc =
          workspace.addScratchResource(asType(saveFilter));
      DepanFxNodeFiltersListDialog.runSaveFilter(
          dialogRunner, filterRsrc);
    }

    @Override
    public Optional<DepanFxListFilterData> runUpdateFilter(
        DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData updateFilter) {
      return DepanFxNodeFiltersListDialog.runUpdateFilter(
          workspace, dialogRunner, asType(updateFilter));
    }

    @Override
    protected Optional<DepanFxListFilterData> runCreateFilter(
        ActionEvent e,
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return DepanFxNodeListChooser.runNodeListChooser(
                workspace, dialogRunner, scene)
            .map(DepanFxListFilterData::createListFilterData);
    }
  }
}
