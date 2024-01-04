package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxCategoryColumnData;
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
public class DepanFxCategoryColumnConfiguration {

  @Bean
  public DepanFxResourceExtMenuContribution categoryColumnExtMenu() {
    return new CategoryColumnExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution categoryColumnPathMenu() {
    return new CategoryColumnPathContribution();
  }

  private static class CategoryColumnExtContribution
      implements DepanFxResourceExtMenuContribution {

    private static Logger LOG =
        LoggerFactory.getLogger(CategoryColumnExtContribution.class);

    @Override
    public boolean acceptsExt(String ext) {
      return DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT.equals(ext);
    }

    @Override
    public void prepareCell(DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace, Cell<DepanFxWorkspaceMember> cell,
        String ext, DepanFxProjectMember member,
        DepanFxContextMenuBuilder builder) {
      Path docPath = member.getMemberPath();
      DepanFxResourcePerspectives.installOnOpen(cell, docPath,
          p -> runCategoryColumnDataAction(dialogRunner, workspace, p));
      builder.appendActionItem(
          DepanFxCategoryColumn.EDIT_CATEGORY_COLUMN,
          e -> runCategoryColumnDataAction(dialogRunner, workspace, docPath));
    }

    private void runCategoryColumnDataAction(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace,
        Path docPath) {
      try {
        workspace.toProjectDocument(docPath.toUri())
            .flatMap(r -> workspace.getWorkspaceResource(
                r, DepanFxCategoryColumnData.class))
            .ifPresent(r -> DepanFxCategoryColumnToolDialog.runEditDialog(
                r.getDocument(), (DepanFxCategoryColumnData) r.getResource(),
                dialogRunner));
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open category column data {} for edit",
            docPath, errCaught);
      }
    }
  }

  private static class CategoryColumnPathContribution
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
      DepanFxCategoryColumn.addNewColumnAction(builder, dialogRunner);
    }
  }
}
