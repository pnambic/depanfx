package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
      implements DepanFxResourceExtMenuContribution {

    private static Logger LOG =
        LoggerFactory.getLogger(FocusColumnExtContribution.class);

    @Override
    public boolean acceptsExt(String ext) {
      return DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT.equals(ext);
    }

    @Override
    public void prepareCell(DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace, Cell<DepanFxWorkspaceMember> cell,
        String ext, DepanFxProjectMember member,
        DepanFxContextMenuBuilder builder) {
      Path docPath = member.getMemberPath();
      DepanFxResourcePerspectives.installOnOpen(cell, docPath,
          p -> runFocusColumnDataAction(dialogRunner, workspace, p));
      builder.appendActionItem(
          DepanFxFocusColumn.EDIT_FOCUS_COLUMN,
          e -> runFocusColumnDataAction(dialogRunner, workspace, docPath));
    }

    private void runFocusColumnDataAction(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace,
        Path docPath) {
      try {
        workspace.toProjectDocument(docPath.toUri())
            .flatMap(workspace::getWorkspaceResource)
            .ifPresent(r -> DepanFxFocusColumnToolDialog.runEditDialog(
                r, dialogRunner, DepanFxFocusColumn.EDIT_FOCUS_COLUMN));
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open focus column data {} for edit",
            docPath, errCaught);
      }
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
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      DepanFxFocusColumn.addNewColumnAction(builder, dialogRunner);
    }
  }
}
