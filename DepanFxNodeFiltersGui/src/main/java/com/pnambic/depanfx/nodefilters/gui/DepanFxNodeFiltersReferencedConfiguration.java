package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxReferencedFilterData;
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
public class DepanFxNodeFiltersReferencedConfiguration {

  public static final String ADD_REFERENCE_FILTER = "Add Reference Filter...";

  public static final String EDIT_REFERENCED_FILTER =
      "Edit Filter Reference...";

  public static final String NEW_REFERENCED_FILTER = "New Filter Reference...";

  private static final String REFERENCED_MATCHER_KEY = "Referenced";

  @Bean
  public DepanFxResourceOpenRegistry.Contribution referencedFilterFileOpenMenu() {
    return new ReferencedFilterFileOpenContribution();
  }

  @Bean
  public DepanFxResourceExtMenuContribution referencedFilterExtMenu() {
    return new ReferencedFilterExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution ReferencedFilterPathMenu() {
    return new ReferencedFilterPathContribution();
  }

  @Bean
  public DepanFxNodeFiltersDialogContribution nodeFilterReferencedContribution(
      DepanFxWorkspace workspace) {
    return new DepanFxNodeFiltersReferencedContribution(workspace);
  }

  private static class ReferencedFilterFileOpenContribution
      extends DepanFxResourceOpenRegistry.Basic<DepanFxReferencedFilterData> {

    public ReferencedFilterFileOpenContribution() {
      super(
          DepanFxReferencedFilterData.class,
          DepanFxReferencedFilterData.REFERENCED_FILTER_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxReferencedFilterData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeFiltersReferencedDialog.runEditFilter(dialogRunner, wkspRsrc);
    }
  }

  private static class ReferencedFilterExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxReferencedFilterData> {

    public ReferencedFilterExtContribution() {
      super(DepanFxReferencedFilterData.class,
          REFERENCED_MATCHER_KEY, EDIT_REFERENCED_FILTER,
          DepanFxReferencedFilterData.REFERENCED_FILTER_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxReferencedFilterData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeFiltersReferencedDialog.runEditFilter(dialogRunner, wkspRsrc);
    }
  }

  private static class ReferencedFilterPathContribution
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
      builder.appendActionItem(NEW_REFERENCED_FILTER,
          e -> runCreateReferencedFilter(workspace, dialogRunner));
    }

    private void runCreateReferencedFilter(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      DepanFxReferencedFilterData filterData =
          DepanFxReferencedFilterData.createReferenceFilterData(null);
      DepanFxNodeFiltersReferencedDialog.runSaveFilter(
          dialogRunner, workspace.addScratchResource(filterData));
    }

    @Override
    public String getOrderKey() {
      return REFERENCED_MATCHER_KEY;
    }
  }

  private static class DepanFxNodeFiltersReferencedContribution
      extends DepanFxNodeFiltersDialogContribution.Basic<DepanFxReferencedFilterData> {

    private final DepanFxWorkspace workspace;

    public DepanFxNodeFiltersReferencedContribution(DepanFxWorkspace workspace) {
      super(REFERENCED_MATCHER_KEY, ADD_REFERENCE_FILTER,
          DepanFxReferencedFilterData.class);
      this.workspace = workspace;
    }

    @Override
    public TreeItem<DepanFxNodeFiltersTableMember> buildTableMember(
        DepanFxNodeFiltersTableMember parentMember,
        DepanFxBaseFilterData filter,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return new DepanFxNodeFiltersReferencedItem(
          new DepanFxNodeFiltersReferencedMember(parentMember, asType(filter)));
    }

    @Override
    public void runSaveFilter(
        DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData saveFilter) {
      DepanFxWorkspaceResource<DepanFxReferencedFilterData> updateRsrc =
          workspace.addScratchResource(asType(saveFilter));
      DepanFxNodeFiltersReferencedDialog.runSaveFilter(
          dialogRunner, updateRsrc);
    }

    @Override
    public Optional<DepanFxReferencedFilterData> runUpdateFilter(
        DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData updateFilter) {
      return DepanFxNodeFiltersReferencedDialog.runUpdateFilter(
          workspace, dialogRunner, asType(updateFilter));
    }

    @Override
    protected Optional<DepanFxReferencedFilterData> runCreateFilter(
        ActionEvent e,
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return DepanFxNodeFiltersChooser.runNodeFiltersFinder(
                workspace, dialogRunner, scene, nodeFiltersDialogRegistry)
            .map(DepanFxReferencedFilterData::createReferenceFilterData);
    }
  }
}
