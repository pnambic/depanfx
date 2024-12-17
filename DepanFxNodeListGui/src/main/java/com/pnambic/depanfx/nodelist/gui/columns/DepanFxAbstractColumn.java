package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseColumnData;
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

public abstract class DepanFxAbstractColumn<T extends DepanFxBaseColumnData>
    implements DepanFxNodeListColumn {

  protected final DepanFxNodeListTableAdapter tableAdapter;

  protected TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> column;

  private DepanFxWorkspaceResource<T> columnDataRsrc;

  public DepanFxAbstractColumn(DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<T> columnDataRsrc) {
    this.tableAdapter = tableAdapter;
    this.columnDataRsrc = columnDataRsrc;
  }

  @Override
  public TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> prepareColumn() {
    TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> result =
        new TreeTableColumn<>(getColumnLabel());
    result.setPrefWidth(getWidthPx());
    result.setContextMenu(buildColumnContextMenu(tableAdapter.getDialogRunner()));

    result.setCellFactory(buildCellFactory());
    result.setCellValueFactory(new ColumnValueFactory());
    result.setSortable(false);
    column = result;
    return result;
  }

  public T getColumnData() {
    return columnDataRsrc.getResource();
  }

  public DepanFxProjectDocument getColumnProjectDoc() {
    return columnDataRsrc.getDocument();
  }

  public double getWidthPx() {
    return DepanFxSceneControls.layoutWidthMs(getWidthMs());
  }

  @Override
  public String getColumnLabel() {
    return getColumnData().getColumnLabel();
  }

  protected double getWidthMs() {
    return getColumnData().getWidthMs();
  }

  // For dialog boxes, especially tool selectors.
  protected Scene getScene() {
    return column.getTreeTableView().getScene();
  }

  /**
   * Derived classes with cached presentation values (e.g. the
   * CategoryEditor member in the category column) should override
   * this method and append their updates.
   */
  protected void refreshColumn() {
    tableAdapter.refreshTableView();
    column.setText(getColumnLabel());
    column.setPrefWidth(getWidthPx());
  }

  protected abstract ContextMenu buildColumnContextMenu(
      DepanFxDialogRunner depanFxDialogRunner);

  /////////////////////////////////////
  // Useful methods for derived types

  /**
   * Not everything that is saved is of type <T>.
   *
   * Some columns save node lists in addition to their own column data.
   */
  protected <R> Optional<DepanFxWorkspaceResource<R>> saveDocument(
      DepanFxProjectDocument projDoc, R item) throws IOException {

      return tableAdapter.getWorkspace().saveDocument(projDoc, item);
  }

  protected void updateColumnDataRsrc(
      DepanFxWorkspaceResource<T> columnDataRsrc) {
    this.columnDataRsrc = columnDataRsrc;
    refreshColumn();
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
      return new DepanFxBaseColumnCell(DepanFxAbstractColumn.this);
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
