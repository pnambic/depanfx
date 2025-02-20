package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
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
public class DepanFxNodeFiltersSequenceConfiguration {

  public static final String ADD_SEQUENCE_FILTER = "Add Sequence Filter";

  public static final String EDIT_SEQUENCE_FILTER =
      "Edit Filter Sequence...";

  public static final String NEW_SEQUENCE_FILTER = "New Filter Sequence...";

  private static final String SEQUENCE_MATCHER_LABEL = "Sequence";

  private static final String SEQUENCE_MATCHER_KEY = "Sequence";

  @Bean
  public DepanFxResourceRegistry.Contribution sequenceFilterFileOpenMenu() {
    return new SequenceFilterFileOpenContribution();
  }

  @Bean
  public DepanFxResourceExtMenuContribution sequenceFilterExtMenu() {
    return new SequenceFilterExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution sequenceFilterPathMenu() {
    return new SequenceFilterPathContribution();
  }

  @Bean
  public DepanFxNodeFiltersDialogContribution nodeFilterSequenceContribution(
      DepanFxWorkspace workspace) {
    return new DepanFxNodeFiltersSequenceContribution(workspace);
  }

  private static class SequenceFilterFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxSequenceFilterData> {

    public SequenceFilterFileOpenContribution() {
      super(
          SEQUENCE_MATCHER_LABEL,
          DepanFxSequenceFilterData.class,
          DepanFxSequenceFilterData.SEQUENCE_FILTER_TOOL_EXT,
          SEQUENCE_MATCHER_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxSequenceFilterData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeFiltersSequenceDialog.runEditFilter(dialogRunner, wkspRsrc);
    }
  }

  private static class SequenceFilterExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxSequenceFilterData> {

    public SequenceFilterExtContribution() {
      super(DepanFxSequenceFilterData.class,
          SEQUENCE_MATCHER_KEY, EDIT_SEQUENCE_FILTER,
          DepanFxSequenceFilterData.SEQUENCE_FILTER_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxSequenceFilterData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeFiltersSequenceDialog.runEditFilter(dialogRunner, wkspRsrc);
    }
  }

  private static class SequenceFilterPathContribution
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
      builder.appendActionItem(NEW_SEQUENCE_FILTER,
          e -> runCreateSequenceFilter(workspace, dialogRunner));
    }

    private void runCreateSequenceFilter(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      DepanFxSequenceFilterData filterData =
          DepanFxSequenceFilterData.createSequenceFilterData();
      DepanFxNodeFiltersSequenceDialog.runSaveFilter(
          dialogRunner, workspace.addScratchResource(filterData));
    }

    @Override
    public String getOrderKey() {
      return SEQUENCE_MATCHER_KEY;
    }
  }

  private static class DepanFxNodeFiltersSequenceContribution
      extends DepanFxNodeFiltersDialogContribution.Basic<DepanFxSequenceFilterData> {

    private final DepanFxWorkspace workspace;


    public DepanFxNodeFiltersSequenceContribution(DepanFxWorkspace workspace) {
      super(SEQUENCE_MATCHER_KEY, ADD_SEQUENCE_FILTER,
          DepanFxSequenceFilterData.class);
      this.workspace = workspace;
    }

    @Override
    public TreeItem<DepanFxNodeFiltersTableMember> buildTableMember(
        DepanFxNodeFiltersTableMember parentMember,
        DepanFxBaseFilterData filter,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return new DepanFxNodeFiltersSequenceItem(
          new DepanFxNodeFiltersSequenceMember(
              parentMember, asType(filter)), nodeFiltersDialogRegistry);
    }

    @Override
    public void runSaveFilter(
        DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData saveFilter) {
      DepanFxWorkspaceResource<DepanFxSequenceFilterData> saveRsrc =
          workspace.addScratchResource(asType(saveFilter));
      DepanFxNodeFiltersSequenceDialog.runSaveFilter(
          dialogRunner, saveRsrc);
    }

    @Override
    public Optional<DepanFxSequenceFilterData> runUpdateFilter(
        DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData updateFilter) {
      return DepanFxNodeFiltersSequenceDialog.runUpdateFilter(
          workspace, dialogRunner, asType(updateFilter));
    }

    @Override
    protected Optional<DepanFxSequenceFilterData> runCreateFilter(
        ActionEvent e,
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
      return Optional.of(DepanFxSequenceFilterData.createSequenceFilterData());
    }
  }
}
