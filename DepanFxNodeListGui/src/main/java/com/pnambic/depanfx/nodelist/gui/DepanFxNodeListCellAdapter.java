package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

public interface DepanFxNodeListCellAdapter {

  DepanFxWorkspace getWorkspace();

  GraphDocument getGraphDoc();

  Stream<DepanFxNodeListColumn> streamColumns();

  void insertSection(
      DepanFxNodeListSection before, DepanFxNodeListSection insert);

  void resetView();

  // Subdialog support
  DepanFxDialogRunner getDialogRunner();

  <T> Dialog<T> buildDialog(Class<T> controllerType);

  // Selection
  void doSelectGraphNodeAction(GraphNode selectNode, boolean value);

  void doSelectGraphNodesAction(Stream<GraphNode> nodes, boolean value);

  ObservableValue<Boolean> getCheckBoxObservable(DepanFxNodeListMember member);

  // Helper
  TreeItem<DepanFxNodeListMember> getTreeItem(int intValue);
}
