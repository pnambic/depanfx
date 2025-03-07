package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData;
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

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeKeyColumnConfiguration {

  public static final String NODE_KEY_COLUMN_LABEL = "Node Key Column";

  public static final String NODE_KEY_COLUMN_KEY = "Node Key Column";

  @Bean
  public DepanFxColumnRegistry.Contribution
      nodeKeyColumnContribution() {
    return new DepanFxColumnRegistry.Basic(
        DepanFxNodeKeyColumnData.class, "Category") {

      @Override
      public DepanFxNodeListColumn toColumn(
          DepanFxNodeListTableAdapter tableAdapter,
          DepanFxWorkspaceResource<?> columnRsrc) {
        @SuppressWarnings("unchecked")
        DepanFxWorkspaceResource<DepanFxNodeKeyColumnData> nodeKeyRsrc =
            (DepanFxWorkspaceResource<DepanFxNodeKeyColumnData>) columnRsrc;
        return new DepanFxNodeKeyColumn(tableAdapter, nodeKeyRsrc);
      }
    };
  }

  @Bean
  public DepanFxResourceRegistry.Contribution nodeKeyColumnFileOpenMenu() {
    return new NodeKeyColumnFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution nodeKeyColumnPathMenu() {
    return new NodeKeyColumnPathContribution();
  }

  private static class NodeKeyColumnFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxNodeKeyColumnData> {

    public NodeKeyColumnFileOpenContribution() {
      super(
          NODE_KEY_COLUMN_LABEL,
          DepanFxNodeKeyColumnData.class,
          DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT,
          NODE_KEY_COLUMN_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxNodeKeyColumnData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeKeyColumnToolDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }

  private static class NodeKeyColumnPathContribution
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
      DepanFxNodeKeyColumn.addNewColumnAction(builder, workspace, dialogRunner);
    }

    @Override
    public String getOrderKey() {
      return NODE_KEY_COLUMN_KEY;
    }
  }
}
