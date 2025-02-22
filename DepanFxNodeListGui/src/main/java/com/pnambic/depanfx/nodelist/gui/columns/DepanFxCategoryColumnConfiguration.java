package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
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

import javafx.scene.control.Cell;

@Configuration
public class DepanFxCategoryColumnConfiguration {

  private static final String CATEGORY_COLUMN_LABEL = "Category Column";

  private static final String CATEGORY_COLUMN_KEY = "Category Column";

  @Bean
  public DepanFxResourceRegistry.Contribution
      categoryColumnFileOpenContribution() {

    return new CategoryColumnFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution categoryColumnPathMenu() {
    return new CategoryColumnPathContribution();
  }

  private static class CategoryColumnFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxCategoryColumnData> {

    private CategoryColumnFileOpenContribution() {
      super(
          CATEGORY_COLUMN_LABEL,
          DepanFxCategoryColumnData.class,
          DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT,
          CATEGORY_COLUMN_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxCategoryColumnData> columnRsrc,
        DepanFxDialogRunner dialogRunner) {

      DepanFxCategoryColumnToolDialog.runEditDialog(
          columnRsrc, dialogRunner, null);
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
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      DepanFxCategoryColumn.addNewColumnAction(builder, dialogRunner, null);
    }

    @Override
    public String getOrderKey() {
      return CATEGORY_COLUMN_KEY;
    }
  }
}
