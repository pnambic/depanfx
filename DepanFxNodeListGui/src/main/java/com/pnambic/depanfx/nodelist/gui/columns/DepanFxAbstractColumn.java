package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.IOException;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
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
    result.setCellValueFactory(p ->
        new ReadOnlyObjectWrapper<>(p.getValue().getValue()));
    result.setSortable(false);
    column = result;
    return result;
  }

  @Override
  public void prepareCell(TreeTableCell<DepanFxNodeListMember, ?> cell) {
    // Mostly nothing to do
  }

  public DepanFxWorkspaceResource<T> getColumnDataResource() {
    return columnDataRsrc;
  }

  public T getColumnData() {
    return columnDataRsrc.getResource();
  }

  public DepanFxProjectDocument getColumnProjectDoc() {
    return columnDataRsrc.getDocument();
  }

  public DepanFxWorkspaceResource<T> forUpdate(T updateInfo) {
    return DepanFxWorkspaceResource.forUpdate(columnDataRsrc, updateInfo);
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

  /////////////////////////////////////
  // Useful methods for derived types

  protected abstract ContextMenu buildColumnContextMenu(
      DepanFxDialogRunner depanFxDialogRunner);

  /**
   * Derived types are expect to override this method to provide
   * a specialized column cell.
   */
  protected Callback<TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
      TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>>
      buildCellFactory() {
    return p -> new DepanFxBaseColumnCell(this);
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
}
