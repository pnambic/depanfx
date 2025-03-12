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
package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry.Contribution;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.columns.infos.DepanFxNodeInfoColumnData;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;
import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;

/**
 * Defines the responsibilities of a node list table, including
 * selection, section, and column management.
 *
 * Node list tables run in multiple contexts
 * - As part of a main screen panel.
 * - As part of a node selection dialog connected to a node view panel.
 * - As part of the node node filter dialog connected to a node view panel.
 */
public interface DepanFxNodeListTableAdapter {

  DepanFxWorkspace getWorkspace();

  GraphDocument getGraphDoc();

  DepanFxNodeList buildEmptyList();

  DepanFxDialogRunner getDialogRunner();

  // Selected node operations
  DepanFxNodeList getSelection();

  ObservableValue<Boolean> getCheckBoxObservable(int treeIndex);

  void doSelectGraphNodeAction(GraphNode selectNode, boolean value);

  void doSelectGraphNodesAction(Stream<GraphNode> nodes, boolean value);

  void refreshTableView();

  // Section operations
  Stream<DepanFxNodeListColumn> streamColumns();

  DepanFxNodeListSection insertSection(
      DepanFxNodeListSection before,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc);

  void updateSection(
      DepanFxNodeListSection section,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc);

  /**
   * Provides original or last assigned table view data.  Any revision to the
   * table view data will be available from
   */
  DepanFxWorkspaceResource<DepanFxNodeListTableViewData> getTableViewResource();

  void setTableViewResource(
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc);

  Optional<DepanFxNodeListColumn> toColumn(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<? extends DepanFxBaseColumnData> columnRsrc);

  Stream<DepanFxColumnRegistry.Contribution> streamColumnChoices();

  Stream<DepanFxInfoRegistry.Contribution> streamInfoChoices();

  String getInfoPropertyString(
      GraphNode graphNode, DepanFxNodeInfoColumnData columnInfo);

  Stream<Contribution> streamInfosByLabel(String label);
}
