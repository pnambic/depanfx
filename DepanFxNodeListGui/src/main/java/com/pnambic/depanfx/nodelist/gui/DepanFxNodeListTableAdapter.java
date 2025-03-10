package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
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

  String getInfoPropertyString(
      GraphNode graphNode, DepanFxNodeInfoColumnData columnInfo);
}
