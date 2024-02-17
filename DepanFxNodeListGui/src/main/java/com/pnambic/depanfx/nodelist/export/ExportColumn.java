/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.nodelist.export;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.export.ExportData.ColumnHandling;
import com.pnambic.depanfx.nodelist.export.ExportData.NodeIdHandling;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxCategoryColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxFocusColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxSimpleColumn;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData.CategoryEntry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExportColumn {

  private final String columnLabel;

  private final Function<GraphNode, String> toValue;

  public ExportColumn(String columnLabel, Function<GraphNode, String> toValue) {
    this.columnLabel = columnLabel;
    this.toValue = toValue;
  }

  public String getColumnLabel() {
    return columnLabel;
  }

  public String toValue(GraphNode node) {
    return toValue.apply(node);
  }

  public static List<ExportColumn> fromExportData(
      NodeIdHandling idHandling, Stream<ExportData> uiData) {
    Builder builder = new ExportColumn.Builder();
    addNodeIdColumns(builder, idHandling);
    uiData
        .filter(x -> x.getHandling() != ColumnHandling.OMIT)
        .forEach(x -> addExportColumns(builder, x));

    return builder.build();
  }

  public static class Builder {

    private final List<ExportColumn> result = new ArrayList<>();

    public void addModelKey() {
      ExportColumn column = new ExportColumn("model-key",
          n -> GraphContextKeys.toNodeKey(n.getId()));
      result.add(column);
    }

    public void addKindKey() {
      ExportColumn column = new ExportColumn("kind-key",
          n -> GraphContextKeys.toNodeKindNodeKey(n.getId()));
      result.add(column);
    }

    public void addNodeKey() {
      ExportColumn column = new ExportColumn("kind-key",
          n -> n.getId().getNodeKey());
      result.add(column);
    }

    public void addTransform(
        String columnLabel, Function<GraphNode, String> toValue) {
      ExportColumn column = new ExportColumn(columnLabel, toValue);
      result.add(column);
    }

    public void addNodeList(
        String columnLabel, String columnValue, DepanFxNodeList nodeList) {
      ExportColumn column = new ExportColumn(columnLabel,
          n -> nodeList.getNodes().contains(n) ? columnValue : "");
      result.add(column);
    }

    public List<ExportColumn> build() {
      return result;
    }
  }

  private static void addNodeIdColumns(
      ExportColumn.Builder builder, NodeIdHandling idHandling) {
    switch(idHandling) {
    case KIND:
      builder.addKindKey();
      return;
    case MODEL:
      builder.addModelKey();
      return;
    case NODE:
      builder.addNodeKey();
      return;
    case PARTS:
      builder.addTransform("model-key",
          n -> n.getId().getContextNodeKindId()
              .getContextModelId().getContextModelKey());
      builder.addTransform("kind-key",
          n -> n.getId().getContextNodeKindId().getNodeKindKey());
      builder.addTransform("node-key",
          n -> n.getId().getNodeKey());
      return;
    default:
      // No other values add export columns.
    }
  }

  private static void addExportColumns(
      Builder builder, ExportData exportData) {
    DepanFxNodeListColumn column = exportData.getColumn();
    if (column instanceof DepanFxCategoryColumn) {
      addCategoryColumnExport(builder, exportData);
      return;
    }
    if (column instanceof DepanFxFocusColumn) {
      DepanFxFocusColumn focusCol = (DepanFxFocusColumn) column;
      DepanFxFocusColumnData columnData = focusCol.getColumnData();
      String columnLabel = columnData.getColumnLabel();
      String focusLabel = columnData.getFocusLabel();
      DepanFxNodeList nodeList =
          (DepanFxNodeList) columnData.getNodeListRsrc().getResource();

      builder.addNodeList(columnLabel, focusLabel, nodeList);
      return;
    }
    if (column instanceof DepanFxNodeKeyColumn) {
      DepanFxNodeKeyColumn nodeKeyCol = (DepanFxNodeKeyColumn) column;
      String columnLabel = nodeKeyCol.getColumnData().getColumnLabel();
      builder.addTransform(columnLabel, n -> nodeKeyCol.toString(n.getId()));
    }
    if (column instanceof DepanFxSimpleColumn) {
      DepanFxSimpleColumn simpleCol = (DepanFxSimpleColumn) column;
      String columnLabel = simpleCol.getColumnData().getColumnLabel();
      builder.addTransform(columnLabel,
          n -> n.getId().getContextNodeKindId().getContextModelId()
          .getContextModelKey());
      return;
    }
  }

  private static void addCategoryColumnExport(
      Builder builder, ExportData exportData) {
    DepanFxCategoryColumn column =
        (DepanFxCategoryColumn) exportData.getColumn();
    DepanFxCategoryColumnData details = column.getColumnData();
    switch (exportData.getHandling()) {
    case EXPAND:
      details.getCategories().stream()
          .forEach(n -> addCategoryEntryExport(builder, n));
      return;
    case INCLUDE:
      builder.addTransform(details.getColumnLabel(),
          n -> reportMultiList(n, details.getCategories()));
      return;
    default:
      // No other values add export columns.
    }
  }

  private static String reportMultiList(GraphNode node, List<CategoryEntry> categories) {
    return categories.stream()
        .filter(c -> containsNode(node, c))
        .map(c -> c.getCategoryLabel())
        .collect(Collectors.joining(", "));
  }

  private static void addCategoryEntryExport(
      Builder builder, CategoryEntry entry) {
    String categoryLabel = entry.getCategoryLabel();
    DepanFxNodeList nodeList =
        (DepanFxNodeList) entry.getNodeListRsrc().getResource();
    builder.addNodeList(nodeList.getNodeListName(), categoryLabel, nodeList);
  }

  private static boolean containsNode(GraphNode node, CategoryEntry entry) {
    DepanFxNodeList nodeList = (DepanFxNodeList) entry.getNodeListRsrc().getResource();
    return nodeList.getNodes().contains(node);
  }
}
