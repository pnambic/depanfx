package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Optional;

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeKeyColumnConfiguration {

  public static final String MODEL_KEY_COLUMN_NAME = "Model Key Column";

  public static final String KIND_KEY_COLUMN_NAME = "Kind Key Column";

  public static final String NODE_KEY_COLUMN_NAME = "Node Column";

  public static final Path MODEL_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(MODEL_KEY_COLUMN_NAME);

  public static final Path KIND_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(KIND_KEY_COLUMN_NAME);

  public static final Path NODE_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(NODE_KEY_COLUMN_NAME);

  private static final String MODEL_KEY_COLUMN_LABEL = "Model Key";

  private static final String KIND_KEY_COLUMN_LABEL = "Kind Key";

  private static final String NODE_KEY_COLUMN_LABEL = "Node Key";

  private static final int COLUMN_WIDTH = 15;

  public static Optional<DepanFxWorkspaceResource> getBuiltinNodeKeyColumnResource(
      DepanFxWorkspace workspace, KeyChoice keyChoice) {
    switch (keyChoice) {
    case MODEL_KEY:
      return getBuiltinNodeKeyColumnResource(workspace, MODEL_KEY_COLUMN_TOOL_PATH);
    case KIND_KEY:
      return getBuiltinNodeKeyColumnResource(workspace, KIND_KEY_COLUMN_TOOL_PATH);
    case NODE_KEY:
      return getBuiltinNodeKeyColumnResource(workspace, NODE_KEY_COLUMN_TOOL_PATH);
    default:
      throw new IllegalArgumentException("Unexpected value: " + keyChoice);
    }
  }

  @Bean
  public DepanFxBuiltInContribution modelKeyColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.MODEL_KEY, MODEL_KEY_COLUMN_LABEL);
    return new DepanFxBuiltInContribution.Simple(
        MODEL_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution kindKeyColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.KIND_KEY, KIND_KEY_COLUMN_LABEL);
    return new DepanFxBuiltInContribution.Simple(
        KIND_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution nodeKeyColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.NODE_KEY, NODE_KEY_COLUMN_LABEL);
    return new DepanFxBuiltInContribution.Simple(
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

  private static Optional<DepanFxWorkspaceResource> getBuiltinNodeKeyColumnResource(
      DepanFxWorkspace workspace, Path toolPath) {
    return DepanFxProjects.getBuiltIn(
        workspace, DepanFxNodeKeyColumnData.class, toolPath);
  }

  private DepanFxNodeKeyColumnData buildNodeKeyColumnData(
      KeyChoice keyChoice, String keyLabel) {
    String columnName = fmtNodeKeyName(keyLabel);
    String columnDescr = fmtNodeKeyDescr(keyLabel);
    return new DepanFxNodeKeyColumnData(
        columnName, columnDescr, keyLabel, COLUMN_WIDTH, keyChoice);
  }

  private String fmtNodeKeyName(String keyLabel) {
    return MessageFormat.format("Built-in {0} Column", keyLabel);
  }

  private String fmtNodeKeyDescr(String keyLabel) {
    return MessageFormat.format("Built-in {0} column.", keyLabel.toLowerCase());
  }

  private static class NodeKeyColumnExtContribution
      extends DepanFxResourceExtMenuContribution.Basic {

    public NodeKeyColumnExtContribution() {
      super(DepanFxNodeKeyColumnData.class,
          DepanFxNodeKeyColumn.EDIT_NODE_KEY_COLUMN,
          DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource wkspRsrc, DepanFxDialogRunner dialogRunner) {
      DepanFxNodeKeyColumnToolDialog.runEditDialog(
          wkspRsrc.getDocument(),
          (DepanFxNodeKeyColumnData) wkspRsrc.getResource(),
          dialogRunner);
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
  }
}
