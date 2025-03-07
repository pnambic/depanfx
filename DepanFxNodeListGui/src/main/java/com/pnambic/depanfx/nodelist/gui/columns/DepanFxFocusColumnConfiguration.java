package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableState;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
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

import javafx.event.Event;
import javafx.scene.control.Cell;

@Configuration
public class DepanFxFocusColumnConfiguration {

  private static final String FOCUS_LABEL = "Focus";

  private static final String FOCUS_KEY = "Focus";

  private static final String FOCUS_COLUMN_LABEL = "Focus Column";

  private static final String FOCUS_COLUMN_KEY = "Focus Column";

  @Bean
  public DepanFxColumnRegistry.Contribution
      focusColumnContribution() {
    return new FocusColumnContribution();
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
      focusColumnFileOpenContribution() {
    return new FocusColumnFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution focusColumnPathMenu() {
    return new FocusColumnPathContribution();
  }

  private static class FocusColumnContribution
      extends DepanFxColumnRegistry.Basic {
    private FocusColumnContribution() {
      super(
          FOCUS_LABEL,
          DepanFxFocusColumnData.class,
          FOCUS_KEY,
          DepanFxFocusColumnToolDialog.FOCUS_COLUMN_RSRC_FILTER);
    }

    @Override
    public DepanFxNodeListColumn toColumn(
        DepanFxNodeListTableAdapter tableAdapter,
        DepanFxWorkspaceResource<?> columnRsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<DepanFxFocusColumnData> focusRsrc =
          (DepanFxWorkspaceResource<DepanFxFocusColumnData>) columnRsrc;
      return new DepanFxFocusColumn(tableAdapter, focusRsrc);
    }

    @Override
    public Optional<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>> getNewColumn(
        Event event,
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxNodeListTableAdapter tableAdapter,
        DepanFxNodeListTableState tableState) {
      DepanFxFocusColumnData columnData =
          DepanFxFocusColumnData.buildInitialFocusColumnData(null);
      DepanFxWorkspaceResource<DepanFxFocusColumnData> columnRsrc =
          workspace.addScratchResource(columnData);
      return DepanFxFocusColumnToolDialog.runCreateDialog(
          columnRsrc, dialogRunner)
          .getController()
          .getToolResource()
          .map(r -> r);
    }
  }

  private static class FocusColumnFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxFocusColumnData> {

    public FocusColumnFileOpenContribution() {
      super(
          FOCUS_COLUMN_LABEL,
          DepanFxFocusColumnData.class,
          DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT,
          FOCUS_COLUMN_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxFocusColumnData> columnRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxFocusColumnToolDialog.runEditDialog(
          columnRsrc, dialogRunner);
    }
  }

  private static class FocusColumnPathContribution
      implements DepanFxResourcePathMenuContribution {

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      DepanFxFocusColumn.addNewColumnAction(builder, dialogRunner, null);
    }

    @Override
    public String getOrderKey() {
      return FOCUS_COLUMN_KEY;
    }
  }
}
