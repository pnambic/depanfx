package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxSimpleColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.text.MessageFormat;

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeListColumnConfiguration {

  private static final String BUILT_IN_SIMPLE_COLUMN_NAME =
      "Built-in Simple Column";

  private static final String BUILT_IN_SIMPLE_COLUMN_DESCR =
      "Built-in simple column.";

  private static final String SIMPLE_COLUMN_LABEL = "Context Model";

  private static final String MODEL_KEY_COLUMN_LABEL = "Model Key";

  private static final String KIND_KEY_COLUMN_LABEL = "Kind Key";

  private static final String NODE_KEY_COLUMN_LABEL = "Node Key";

  private static final int COLUMN_WIDTH = 15;

  @Bean
  public DepanFxBuiltInContribution simpleColumn() {
    DepanFxSimpleColumnData toolData = new DepanFxSimpleColumnData(
        BUILT_IN_SIMPLE_COLUMN_NAME, BUILT_IN_SIMPLE_COLUMN_DESCR,
        SIMPLE_COLUMN_LABEL, COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple(
        DepanFxNodeListColumnData.SIMPLE_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution modelKeyColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.MODEL_KEY, MODEL_KEY_COLUMN_LABEL);
    return new DepanFxBuiltInContribution.Simple(
        DepanFxNodeListColumnData.MODEL_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution kindKeyColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.KIND_KEY, KIND_KEY_COLUMN_LABEL);
    return new DepanFxBuiltInContribution.Simple(
        DepanFxNodeListColumnData.KIND_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution nodeKeyColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.NODE_KEY, NODE_KEY_COLUMN_LABEL);
    return new DepanFxBuiltInContribution.Simple(
        DepanFxNodeListColumnData.NODE_KEY_COLUMN_TOOL_PATH, toolData);
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
      implements DepanFxResourceExtMenuContribution {

    private static Logger LOG =
        LoggerFactory.getLogger(NodeKeyColumnExtContribution.class);

    @Override
    public boolean acceptsExt(String ext) {
      return DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT.equals(ext);
    }

    @Override
    public void prepareCell(DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace, Cell<DepanFxWorkspaceMember> cell,
        String ext, DepanFxProjectMember member,
        DepanFxContextMenuBuilder builder) {
      Path docPath = member.getMemberPath();
      DepanFxResourcePerspectives.installOnOpen(cell, docPath,
          p -> runNodeKeyColumnDataAction(dialogRunner, workspace, p));
      builder.appendActionItem(
          DepanFxNodeKeyColumn.EDIT_NODE_KEY_COLUMN,
          e -> runNodeKeyColumnDataAction(dialogRunner, workspace, docPath));
    }

    private void runNodeKeyColumnDataAction(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace,
        Path docPath) {
      try {
        workspace.toProjectDocument(docPath.toUri())
            .flatMap(workspace::getWorkspaceResource)
            .ifPresent(r -> DepanFxNodeKeyColumnToolDialog.runEditDialog(
                r, dialogRunner, DepanFxNodeKeyColumn.EDIT_NODE_KEY_COLUMN));
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open node key column data {} for edit",
            docPath, errCaught);
      }
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
