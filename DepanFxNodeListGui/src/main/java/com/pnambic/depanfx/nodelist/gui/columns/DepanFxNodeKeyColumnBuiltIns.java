/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.text.MessageFormat;

@Configuration
public class DepanFxNodeKeyColumnBuiltIns {

  public static final String MODEL_KEY_COLUMN_NAME = "Model Key Column";

  public static final String KIND_KEY_COLUMN_NAME = "Kind Key Column";

  public static final String NODE_KEY_COLUMN_NAME = "Node Key Column";

  public static final String SIMPLE_NAME_COLUMN_NAME = "Simple Name Column";

  public static final Path MODEL_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(MODEL_KEY_COLUMN_NAME);

  public static final Path KIND_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(KIND_KEY_COLUMN_NAME);

  public static final Path NODE_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(NODE_KEY_COLUMN_NAME);

  private static final Path SIMPLE_NAME_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(SIMPLE_NAME_COLUMN_NAME);

  private static final String MODEL_KEY_COLUMN_LABEL = "Model Key";

  private static final String KIND_KEY_COLUMN_LABEL = "Kind Key";

  private static final String NODE_KEY_COLUMN_LABEL = "Node Key";

  private static final String SIMPLE_NAME_COLUMN_LABEL = "Short Name";

  private static final int MODEL_KEY_COLUMN_WIDTH = 8;

  private static final int KIND_KEY_COLUMN_WIDTH = 8;

  private static final int NODE_KEY_COLUMN_WIDTH = 15;

  private static final int SIMPLE_NAME_COLUMN_WIDTH = 12;

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
  public DepanFxBuiltInContribution<DepanFxNodeKeyColumnData> simpleNameColumn() {
    DepanFxNodeKeyColumnData toolData = buildNodeKeyColumnData(
        KeyChoice.SIMPLE_NAME, SIMPLE_NAME_COLUMN_LABEL, SIMPLE_NAME_COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple<>(
        SIMPLE_NAME_COLUMN_TOOL_PATH, toolData);
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
}
