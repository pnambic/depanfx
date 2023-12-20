package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.IOException;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public abstract class DepanFxAbstractColumn
    implements DepanFxNodeListColumn {

  protected final DepanFxNodeListViewer listViewer;

  protected TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> column;

  public DepanFxAbstractColumn(DepanFxNodeListViewer listViewer) {
    super();
    this.listViewer = listViewer;
  }

  @Override
  public TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> prepareColumn() {
    TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> result =
        new TreeTableColumn<>(getColumnLabel());
    result.setPrefWidth(getWidthPx());
    result.setContextMenu(buildColumnContextMenu(listViewer.getDialogRunner()));

    result.setCellFactory(buildCellFactory());
    result.setCellValueFactory(new ColumnValueFactory());
    result.setSortable(false);
    column = result;
    return result;
  }

  public double getWidthPx() {
    return DepanFxSceneControls.layoutWidthMs(getWidthMs());
  }

  // For dialog boxes, especially tool selectors.
  protected Scene getScene() {
    return column.getTreeTableView().getScene();
  }

  protected void refreshColumn() {
    listViewer.refreshView();
    column.setText(getColumnLabel());
    column.setPrefWidth(getWidthPx());
  }

  protected abstract double getWidthMs();

  protected abstract ContextMenu buildColumnContextMenu(
      DepanFxDialogRunner depanFxDialogRunner);

  /////////////////////////////////////
  // Useful methods for derived types

  protected Optional<DepanFxWorkspaceResource> saveDocument(
      DepanFxProjectDocument projDoc, Object item) throws IOException {

      return listViewer.getWorkspace().saveDocument(projDoc, item);
  }

  /////////////////////////////////////
  // Internal Types

  protected Callback<TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
      TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>>
      buildCellFactory() {
    return new ColumnCellFactory();
  }

  class ColumnCellFactory implements
      Callback<TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
      TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>> {

    @Override
    public TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> call(
        TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> param) {
      return new DepanFxSimpleColumnCell(DepanFxAbstractColumn.this);
    }
  }

  class ColumnValueFactory implements
      Callback<TreeTableColumn.CellDataFeatures<
          DepanFxNodeListMember, DepanFxNodeListMember>,
      ObservableValue<DepanFxNodeListMember>> {

    @Override
    public ObservableValue<DepanFxNodeListMember> call(
        TreeTableColumn.CellDataFeatures<
            DepanFxNodeListMember, DepanFxNodeListMember> param) {
      return new ReadOnlyObjectWrapper<>(param.getValue().getValue());
    }
  }
}
