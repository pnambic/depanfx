package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxReferencedFilterData;
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
public class DepanFxNodeFiltersReferencedConfiguration {

  public static final String EDIT_REFERENCED_FILTER =
      "Edit Filter Reference...";

  public static final String NEW_REFERENCED_FILTER = "New Filter Reference...";

  private static final String REFERENCED_MATCHER_KEY = "Referenced";

  @Bean
  public DepanFxResourceExtMenuContribution referencedFilterExtMenu() {
    return new ReferencedFilterExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution ReferencedFilterPathMenu() {
    return new ReferencedFilterPathContribution();
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
          e -> runCreateReferencedFilter(dialogRunner));
    }

    private void runCreateReferencedFilter(DepanFxDialogRunner dialogRunner) {
      DepanFxReferencedFilterData filterData =
          DepanFxReferencedFilterData.createReferenceFilterData(null);
      DepanFxNodeFiltersReferencedDialog.runSaveFilter(dialogRunner, filterData);
    }

    @Override
    public String getOrderKey() {
      return REFERENCED_MATCHER_KEY;
    }
  }
}
