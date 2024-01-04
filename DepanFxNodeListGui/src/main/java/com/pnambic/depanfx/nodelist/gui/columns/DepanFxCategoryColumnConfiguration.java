package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
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
      extends DepanFxResourceExtMenuContribution.Basic {

    public CategoryColumnExtContribution() {
      super(DepanFxCategoryColumnData.class,
          DepanFxCategoryColumn.EDIT_CATEGORY_COLUMN,
          DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource wkspRsrc, DepanFxDialogRunner dialogRunner) {
      DepanFxCategoryColumnToolDialog.runEditDialog(
          wkspRsrc.getDocument(),
          (DepanFxCategoryColumnData) wkspRsrc.getResource(),
          dialogRunner);
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
