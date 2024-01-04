package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxFocusColumnData;
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
public class DepanFxFocusColumnConfiguration {

  @Bean
  public DepanFxResourceExtMenuContribution focusColumnExtMenu() {
    return new FocusColumnExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution focusColumnPathMenu() {
    return new FocusColumnPathContribution();
  }

  private static class FocusColumnExtContribution
      extends DepanFxResourceExtMenuContribution.Basic {

    public FocusColumnExtContribution() {
      super(DepanFxFocusColumnData.class,
          DepanFxFocusColumn.EDIT_FOCUS_COLUMN,
          DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource wkspRsrc, DepanFxDialogRunner dialogRunner) {
      DepanFxFocusColumnToolDialog.runEditDialog(
          wkspRsrc.getDocument(),
          (DepanFxFocusColumnData) wkspRsrc.getResource(),
          dialogRunner);
    }
  }

  private static class FocusColumnPathContribution
      implements DepanFxResourcePathMenuContribution {

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      DepanFxFocusColumn.addNewColumnAction(builder, dialogRunner);
    }
  }
}
