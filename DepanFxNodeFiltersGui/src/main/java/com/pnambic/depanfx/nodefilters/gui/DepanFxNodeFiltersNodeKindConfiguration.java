package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeKindFilterData;
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
public class DepanFxNodeFiltersNodeKindConfiguration {

  public static final String ADD_NODE_KIND_FILTER = "Add Node Kind Filter...";

  public static final String EDIT_NODE_KIND_FILTER =
      "Edit Node Kind Filter...";

  public static final String NEW_NODE_KIND_FILTER = "New Node Kind Filter...";

  private static final String NODE_KIND_KEY = "Node Kind";

  @Bean
  public DepanFxResourceExtMenuContribution nodeKindFilterExtMenu() {
    return new NodeKindFilterExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution nodeKindFilterPathMenu() {
    return new NodeKindFilterPathContribution();
  }

  @Bean
  public DepanFxNodeFiltersDialogContribution nodeFilterNodeKindContribution() {
    return new NodeKindNodeFiltersContribution();
  }

  private static class NodeKindFilterExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxNodeKindFilterData> {

    public NodeKindFilterExtContribution() {
      super(DepanFxNodeKindFilterData.class,
          NODE_KIND_KEY, EDIT_NODE_KIND_FILTER,
          DepanFxNodeKindFilterData.NODE_KIND_FILTER_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxNodeKindFilterData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      // DepanFxNodeFiltersMatcherDialog.runEditDialog(dialogRunner, wkspRsrc);
    }
  }

  private static class NodeKindFilterPathContribution
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
      builder.appendActionItem(NEW_NODE_KIND_FILTER,
          e -> runCreateNodeKindFilter(dialogRunner));
    }

    private void runCreateNodeKindFilter(DepanFxDialogRunner dialogRunner) {
      DepanFxNodeKindFilterData filterData =
          DepanFxNodeKindFilterData.createNodeKindFilterData(null);
      // DepanFxNodeFiltersMatcherDialog.runSaveFilter(dialogRunner, filterData);
    }

    @Override
    public String getOrderKey() {
      return NODE_KIND_KEY;
    }
  }

  private static class NodeKindNodeFiltersContribution
      extends DepanFxNodeFiltersDialogContribution.Basic<DepanFxNodeKindFilterData> {

    public NodeKindNodeFiltersContribution() {
      super(NODE_KIND_KEY, ADD_NODE_KIND_FILTER,
          DepanFxNodeKindFilterData.class);
    }

    @Override
    public TreeItem<DepanFxNodeFiltersTableMember> buildTableMember(
        DepanFxNodeFiltersTableMember parentMember,
        DepanFxBaseFilterData filter,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return new DepanFxNodeFiltersNodeKindItem(
          new DepanFxNodeFiltersNodeKindMember(parentMember, asType(filter)));
    }

    @Override
    public void runSaveFilter(DepanFxDialogRunner dialogRunner,
        DepanFxBaseFilterData saveFilter) {
      // DepanFxNodeFiltersMatcherDialog.runSaveFilter(
      //    dialogRunner, asType(saveFilter));
    }

    @Override
    public Optional<DepanFxNodeKindFilterData> runUpdateFilter(
        DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData updateFilter) {
      return Optional.empty();
      // return DepanFxNodeFiltersMatcherDialog.runUpdateFilter(
      //     dialogRunner, asType(updateFilter));
    }

    @Override
    protected Optional<DepanFxNodeKindFilterData> runCreateFilter(
        ActionEvent e,
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return Optional.empty();
      // return DepanFxLinkMatcherChooser.runLinkMatcherFinder(
      //           workspace, dialogRunner, scene)
      //      .map(DepanFxMatcherFilterData::createNodeKindFilterData);
    }
  }
}
