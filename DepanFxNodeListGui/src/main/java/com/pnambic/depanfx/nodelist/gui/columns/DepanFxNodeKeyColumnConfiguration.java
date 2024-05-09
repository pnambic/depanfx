package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.text.MessageFormat;

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeKeyColumnConfiguration {

  public static final String MODEL_KEY_COLUMN_NAME = "Model Key Column";

  public static final String KIND_KEY_COLUMN_NAME = "Kind Key Column";

  public static final String NODE_KEY_COLUMN_NAME = "Node Key Column";

  public static final Path MODEL_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(MODEL_KEY_COLUMN_NAME);

  public static final Path KIND_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(KIND_KEY_COLUMN_NAME);

  public static final Path NODE_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(NODE_KEY_COLUMN_NAME);

  private static final String NODE_KEY_COLUMN_KEY = "Node Key Column";

  private static final String MODEL_KEY_COLUMN_LABEL = "Model Key";

  private static final String KIND_KEY_COLUMN_LABEL = "Kind Key";

  private static final String NODE_KEY_COLUMN_LABEL = "Node Key";

  private static final int MODEL_KEY_COLUMN_WIDTH = 8;

  private static final int KIND_KEY_COLUMN_WIDTH = 8;

  private static final int NODE_KEY_COLUMN_WIDTH = 15;

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKeyColumnData> modelKeyColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.MODEL_KEY, MODEL_KEY_COLUMN_LABEL, MODEL_KEY_COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple<>(
        MODEL_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKeyColumnData> kindKeyColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.KIND_KEY, KIND_KEY_COLUMN_LABEL, KIND_KEY_COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple<>(
        KIND_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKeyColumnData> nodeKeyColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.NODE_KEY, NODE_KEY_COLUMN_LABEL, NODE_KEY_COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple<>(
        NODE_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxResourceExtMenuContribution nodeKeyColumnExtMenu() {
    return new NodeKeyColumnExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution nodeKeyColumnPathMenu() {
    return new NodeKeyColumnPathContribution();
  }

  private DepanFxNodeKeyColumnData buildNodeKeyColumnData(
      KeyChoice keyChoice, String keyLabel, int columnWidth) {
    String columnName = fmtNodeKeyName(keyLabel);
    String columnDescr = fmtNodeKeyDescr(keyLabel);
    return new DepanFxNodeKeyColumnData(
        columnName, columnDescr, keyLabel, columnWidth, keyChoice);
  }

  private String fmtNodeKeyName(String keyLabel) {
    return MessageFormat.format("Built-in {0} Column", keyLabel);
  }

  private String fmtNodeKeyDescr(String keyLabel) {
    return MessageFormat.format("Built-in {0} column.", keyLabel.toLowerCase());
  }

  private static class NodeKeyColumnExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxNodeKeyColumnData> {

    public NodeKeyColumnExtContribution() {
      super(DepanFxNodeKeyColumnData.class, NODE_KEY_COLUMN_KEY,
          DepanFxNodeKeyColumn.EDIT_NODE_KEY_COLUMN,
          DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxNodeKeyColumnData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeKeyColumnToolDialog.runEditDialog(
          wkspRsrc.getDocument(), wkspRsrc.getResource(), dialogRunner);
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
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      DepanFxNodeKeyColumn.addNewColumnAction(builder, dialogRunner);
    }

    @Override
    public String getOrderKey() {
      return NODE_KEY_COLUMN_KEY;
    }
  }
}
