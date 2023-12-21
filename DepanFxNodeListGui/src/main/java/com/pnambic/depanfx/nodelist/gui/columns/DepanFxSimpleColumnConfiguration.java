package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxSimpleColumnData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Optional;

@Configuration
public class DepanFxSimpleColumnConfiguration {

  public static final String SIMPLE_COLUMN_NAME = "Simple Column";

  public static final Path SIMPLE_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(SIMPLE_COLUMN_NAME);

  private static final String BUILT_IN_SIMPLE_COLUMN_NAME =
      "Built-in Simple Column";

  private static final String BUILT_IN_SIMPLE_COLUMN_DESCR =
      "Built-in simple column.";

  private static final String SIMPLE_COLUMN_LABEL = "Context Model";

  private static final int COLUMN_WIDTH = 15;

  public static Optional<DepanFxWorkspaceResource> getBuiltinSimpleColumnResource(
      DepanFxWorkspace workspace) {
    return DepanFxProjects.getBuiltIn(
        workspace, DepanFxSimpleColumnData.class,
        c -> c.getPath().equals(SIMPLE_COLUMN_TOOL_PATH));
  }

  @Bean
  public DepanFxBuiltInContribution simpleColumn() {
    DepanFxSimpleColumnData toolData = new DepanFxSimpleColumnData(
        BUILT_IN_SIMPLE_COLUMN_NAME, BUILT_IN_SIMPLE_COLUMN_DESCR,
        SIMPLE_COLUMN_LABEL, COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple(
        SIMPLE_COLUMN_TOOL_PATH, toolData);
  }
}
