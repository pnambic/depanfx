package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;
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
public class DepanFxNodeFiltersSequenceConfiguration {

  public static final String EDIT_SEQUENCE_FILTER =
      "Edit Filter Sequence...";

  public static final String NEW_SEQUENCE_FILTER = "New Filter Sequence...";

  private static final String SEQUENCE_MATCHER_KEY = "Sequence";

  @Bean
  public DepanFxResourceExtMenuContribution sequenceFilterExtMenu() {
    return new SequenceFilterExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution sequenceFilterPathMenu() {
    return new SequenceFilterPathContribution();
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
          e -> runCreateSequenceFilter(dialogRunner));
    }

    private void runCreateSequenceFilter(DepanFxDialogRunner dialogRunner) {
      DepanFxSequenceFilterData filterData =
          DepanFxSequenceFilterData.createSequenceFilterData();
      DepanFxNodeFiltersSequenceDialog.runSaveFilter(dialogRunner, filterData);
    }

    @Override
    public String getOrderKey() {
      return SEQUENCE_MATCHER_KEY;
    }
  }
}
