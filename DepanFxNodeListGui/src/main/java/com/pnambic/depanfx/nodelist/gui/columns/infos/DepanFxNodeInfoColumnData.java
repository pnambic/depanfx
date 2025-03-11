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

import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;

import java.util.stream.Stream;

public class DepanFxNodeInfoColumnData extends DepanFxBaseColumnData {

  public static final String NEW_INFO_COLUMN_NAME = "Info";

  public static final String NEW_INFO_COLUMN_DESCR = "New node info column.";

  public static final String NEW_INFO_COLUMN_LABEL = "Info";

  public static final int COLUMN_WIDTH_MS = 15;

  public static final String NODE_INFO_COLUMN_TOOL_EXT = "dnicti";

  private final DepanFxInfoRegistry.Contribution infoContribution;

  private final DepanFxNodeInfoProperty infoProperty;

  public DepanFxNodeInfoColumnData(
      String toolName, String toolDescription,
      String columnLabel, int widthMs,
      DepanFxInfoRegistry.Contribution infoContribution,
      DepanFxNodeInfoProperty infoProperty) {
    super(toolName, toolDescription, columnLabel, widthMs);
    this.infoContribution = infoContribution;
    this.infoProperty = infoProperty;
  }

  public static DepanFxNodeInfoColumnData buildInitialColumnData() {
    return new DepanFxNodeInfoColumnData(
        NEW_INFO_COLUMN_NAME, NEW_INFO_COLUMN_DESCR,
        NEW_INFO_COLUMN_LABEL, COLUMN_WIDTH_MS,
        null, null);
  }

  public DepanFxInfoRegistry.Contribution getInfoContribution() {
    return infoContribution;
  }

  public DepanFxNodeInfoProperty getInfoProperty() {
    return infoProperty;
  }

  public Stream<DepanFxNodeInfoProperty> streamProperties() {
    return infoContribution.streamProperties();
  }
}
