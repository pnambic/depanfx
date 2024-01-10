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

import com.pnambic.depanfx.graph.model.GraphNode;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCsvExporter {

  private final List<ExportColumn> exportColumns;

  private CSVPrinter exporter;

  public AbstractCsvExporter(List<ExportColumn> exportColumns) {
    this.exportColumns = exportColumns;
  }

  /**
   * Caller is responsible to close the writer.
   * A try-resource block is recommended.
   */
  public void export(Writer dest, Collection<GraphNode> exportRoots)
      throws IOException {
    this.exporter = new CSVPrinter(dest, CSVFormat.DEFAULT);
    writeHeader();
    writeData(exportRoots);
  }

  /**
   * The derived class should start with the supplied {@code exportRoots}
   * to decide which, if any, CSV data rows will be written.  The output
   * may include all, some, or none of the {@code exportRoots}, and additional
   * data rows from other nodes may be included.
   *
   * For example, the tree section exporter expands the set of output rows to
   * include the descendants of the {@code exportRoots}.
   *
   * @param exportRoots starting point for the CSV data rows.
   * @throws IOException forwarded from below
   */
  protected abstract void writeData(Collection<GraphNode> exportRoots)
      throws IOException;

  protected void writeRow(GraphNode node) throws IOException {
    for (ExportColumn column : exportColumns) {
      String value = column.toValue(node); 
      exporter.print(value);
    }
    exporter.println();
  }

  private void writeHeader() throws IOException {
    for (ExportColumn column : exportColumns) {
      exporter.print(column.getColumnLabel());
    }
    exporter.println();
  }
}
