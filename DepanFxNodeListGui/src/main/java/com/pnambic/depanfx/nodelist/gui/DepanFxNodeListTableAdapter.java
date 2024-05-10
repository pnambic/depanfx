package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;

/**
 * Defines the responsibilities of a node list table, including
 * selection, section, and column management.
 */
public interface DepanFxNodeListTableAdapter {

  DepanFxWorkspace getWorkspace();

  GraphDocument getGraphDoc();

  void refreshTableView();

  void setTableView(DepanFxNodeListTableViewData tableView);

  DepanFxNodeListTableViewData getTableView();

  // Column columns
  void addColumn(
      DepanFxWorkspaceResource<? extends DepanFxBaseColumnData> columnRsrc);

  Stream<DepanFxNodeListColumn> streamColumns();

  // Section operations
  DepanFxNodeListSection insertSection(
      DepanFxNodeListSection before,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc);

  void updateSection(
      DepanFxNodeListSection section,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc);

  // Subdialog support
  Scene getScene();

  DepanFxDialogRunner getDialogRunner();

  <T> Dialog<T> buildDialog(Class<T> controllerType);

  // Selection operations
  void doSelectAllAction();

  void doClearSelectionAction();

  void doInvertSelectionAction();

  void doSelectGraphNodeAction(GraphNode selectNode, boolean value);

  void doSelectGraphNodesAction(Stream<GraphNode> nodes, boolean value);

  ObservableValue<Boolean> getCheckBoxObservable(DepanFxNodeListMember member);

  DepanFxNodeList getSelection();

  // Helper
  TreeItem<DepanFxNodeListMember> getTreeItem(int intValue);
}
