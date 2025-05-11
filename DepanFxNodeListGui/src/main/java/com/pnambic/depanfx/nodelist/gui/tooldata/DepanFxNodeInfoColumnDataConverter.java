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
package com.pnambic.depanfx.nodelist.gui.tooldata;

import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.nodelist.gui.columns.infos.DepanFxNodeInfoColumnData;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DepanFxNodeInfoColumnDataConverter
    extends BasePersistObjectConverter<DepanFxNodeInfoColumnData> {

  public static final String NODE_INFO_COLUMN_INFO_TAG = "info-column-info";

  private static final Class<?>[] ALLOWED_TYPES = new Class[] {
      DepanFxNodeInfoColumnData.class,
      NodeInfoColumnDataTransport.class
  };

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeInfoColumnDataConverter.class);

  private final DepanFxInfoRegistry infoRegistry;

  public DepanFxNodeInfoColumnDataConverter(DepanFxInfoRegistry registry) {
    this.infoRegistry = registry;
  }

  @Override
  public Class<?> forType() {
    return DepanFxNodeInfoColumnData.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOWED_TYPES;
  }

  @Override
  public String getTag() {
    return NODE_INFO_COLUMN_INFO_TAG;
  }

  @Override
  public void marshal(XstreamMarshalContext dstContext, Object source) {
    DepanFxNodeInfoColumnData columInfo = (DepanFxNodeInfoColumnData) source;

    marshalValue(dstContext, new NodeInfoColumnDataTransport(columInfo));
  }

  @Override
  public DepanFxNodeInfoColumnData unmarshal(
      XstreamUnmarshalContext srcContext) {
    NodeInfoColumnDataTransport columnTransport =
        (NodeInfoColumnDataTransport) srcContext.convertAnother(
            null, NodeInfoColumnDataTransport.class);

    Optional<DepanFxInfoRegistry.Contribution> optInfoKind =
        infoRegistry.getById(columnTransport.infoContribution);
    Optional<DepanFxNodeInfoProperty> optInfoProperty =
        optInfoKind.flatMap(c -> c.getProperty(columnTransport.infoProperty));

    if (optInfoKind.isEmpty()) {
      LOG.error("Unable to find info kind {}",
          columnTransport.infoContribution);
    }

    if (optInfoProperty.isEmpty()) {
      LOG.error("Unable to find info property {}",
          columnTransport.infoProperty);
    }
    return new DepanFxNodeInfoColumnData(
        columnTransport.toolName,
        columnTransport.toolDescription,
        columnTransport.columnLabel,
        columnTransport.widthMs,
        null, // info source
        optInfoKind.orElse(null),
        optInfoProperty.orElse(null));
  }

  public static class NodeInfoColumnDataTransport {

    public String toolName;

    public String toolDescription;

    public String columnLabel;

    public int widthMs;

    public String infoContribution;

    public String infoProperty;

    public NodeInfoColumnDataTransport(DepanFxNodeInfoColumnData columnInfo) {
      this.toolName = columnInfo.getToolName();
      this.toolDescription = columnInfo.getToolDescription();
      this.columnLabel = columnInfo.getColumnLabel();
      this.widthMs = columnInfo.getWidthMs();
      this.infoContribution = columnInfo.getInfoContribution().getInfoId();
      this.infoProperty = columnInfo.getInfoProperty().getToolName();
    }
  }
}
