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
package com.pnambic.depanfx.nodelist.gui.columns.infos;

import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeKeyInfoContribution;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.text.MessageFormat;

@Configuration
public class DepanFxNodeKeyColumnBuiltIns {

  private static final String MODEL_KEY_COLUMN_LABEL = "Model Key";

  public static final String MODEL_KEY_COLUMN_NAME = "Model Key Column";

  private static final int MODEL_KEY_COLUMN_WIDTH = 8;

  public static final Path MODEL_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(MODEL_KEY_COLUMN_NAME);

  private static final String KIND_KEY_COLUMN_LABEL = "Kind Key";

  public static final String KIND_KEY_COLUMN_NAME = "Kind Key Column";

  private static final int KIND_KEY_COLUMN_WIDTH = 8;

  public static final Path KIND_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(KIND_KEY_COLUMN_NAME);

  public static final String NODE_KEY_COLUMN_NAME = "Node Key Column";

  private static final String NODE_KEY_COLUMN_LABEL = "Node Key";

  private static final int NODE_KEY_COLUMN_WIDTH = 15;

  public static final Path NODE_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(NODE_KEY_COLUMN_NAME);

  private static final String SIMPLE_NAME_COLUMN_LABEL = "Simple Name";

  public static final String SIMPLE_NAME_COLUMN_NAME = "Simple Name Column";

  private static final int SIMPLE_NAME_COLUMN_WIDTH = 12;

  private static final Path SIMPLE_NAME_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(SIMPLE_NAME_COLUMN_NAME);

  private static final String FULL_KEY_COLUMN_LABEL = "Full Key";

  public static final String FULL_KEY_COLUMN_NAME = "Full Key Column";

  private static final int FULL_KEY_COLUMN_WIDTH = 12;

  private static final Path FULL_KEY_COLUMN_TOOL_PATH =
      DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.resolve(FULL_KEY_COLUMN_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeInfoColumnData> modelKeyColumn(
      DepanFxNodeKeyInfoContribution infoContrib) {
    DepanFxNodeInfoColumnData toolData = buildInfoColumn(
        MODEL_KEY_COLUMN_LABEL, infoContrib,
        DepanFxNodeKeyInfoContribution.GRAPH_MODEL_PROPERTY,
        MODEL_KEY_COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple<>(
        MODEL_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeInfoColumnData> kindKeyColumn(
      DepanFxNodeKeyInfoContribution infoContrib) {
    DepanFxNodeInfoColumnData toolData = buildInfoColumn(
        KIND_KEY_COLUMN_LABEL, infoContrib,
        DepanFxNodeKeyInfoContribution.NODE_KIND_PROPERTY,
        KIND_KEY_COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple<>(
        KIND_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeInfoColumnData> nodeKeyColumn(
      DepanFxNodeKeyInfoContribution infoContrib) {
    DepanFxNodeInfoColumnData toolData = buildInfoColumn(
        NODE_KEY_COLUMN_LABEL, infoContrib,
        DepanFxNodeKeyInfoContribution.NODE_KEY_PROPERTY,
        NODE_KEY_COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple<>(
        NODE_KEY_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeInfoColumnData> simpleNameColumn(
      DepanFxNodeKeyInfoContribution infoContrib) {
    DepanFxNodeInfoColumnData toolData = buildInfoColumn(
        SIMPLE_NAME_COLUMN_LABEL, infoContrib,
        DepanFxNodeKeyInfoContribution.SIMPLE_NAME_PROPERTY,
        SIMPLE_NAME_COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple<>(
        SIMPLE_NAME_COLUMN_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeInfoColumnData> fullKeyColumn(
      DepanFxNodeKeyInfoContribution infoContrib) {
    DepanFxNodeInfoColumnData toolData = buildInfoColumn(
        FULL_KEY_COLUMN_LABEL, infoContrib,
        DepanFxNodeKeyInfoContribution.FULL_KEY_PROPERTY,
        FULL_KEY_COLUMN_WIDTH);
    return new DepanFxBuiltInContribution.Simple<>(
        FULL_KEY_COLUMN_TOOL_PATH, toolData);
  }

  private DepanFxNodeInfoColumnData buildInfoColumn(
      String keyLabel,
      DepanFxNodeKeyInfoContribution infoContrib,
      DepanFxNodeInfoProperty infoProperty,
      int columnWidth) {
    String columnName = fmtNodeKeyName(keyLabel);
    String columnDescr = fmtNodeKeyDescr(keyLabel);
    return new DepanFxNodeInfoColumnData(
        columnName, columnDescr, keyLabel, columnWidth,
        infoContrib, infoProperty);
  }

  private String fmtNodeKeyName(String keyLabel) {
    return MessageFormat.format("Built-in {0} Column", keyLabel);
  }

  private String fmtNodeKeyDescr(String keyLabel) {
    return MessageFormat.format("Built-in {0} column.", keyLabel.toLowerCase());
  }
}
