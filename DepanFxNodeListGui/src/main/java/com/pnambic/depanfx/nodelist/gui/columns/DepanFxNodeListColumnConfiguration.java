package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxSimpleColumnData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;

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
}
