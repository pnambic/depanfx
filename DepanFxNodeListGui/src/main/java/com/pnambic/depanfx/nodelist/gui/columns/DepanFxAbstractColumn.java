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

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;

/**
 * Use column data to configure a column in a node list table.
 *
 * Coordinate a resource of column data with column in a node list table.
 * Handle serialization of an updated column (e.g. width or other user setting).
 */
public abstract class DepanFxAbstractColumn<T extends DepanFxBaseColumnData>
    implements DepanFxNodeListColumn {

  protected final DepanFxNodeListTableAdapter tableAdapter;

  protected TreeTableColumn<DepanFxNodeListMember, ?> column;

  private DepanFxWorkspaceResource<T> columnDataRsrc;

  public DepanFxAbstractColumn(DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<T> columnDataRsrc) {
    this.tableAdapter = tableAdapter;
    this.columnDataRsrc = columnDataRsrc;
  }

  @Override
  /**
   * Creates the column with the chosen configuration.
   *
   * @return GUI column entityfor this node list column.
   */
  public TreeTableColumn<DepanFxNodeListMember, ?> createColumn() {
    column = buildColumn();

    column.setPrefWidth(getWidthPx());
    column.setSortable(false);

    column.setContextMenu(
        buildColumnContextMenu(tableAdapter.getDialogRunner()));
    return column;
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

  @Override
  public String getColumnLabel() {
    return getColumnData().getColumnLabel();
  }

  // For dialog boxes, especially tool selectors.
  protected Scene getScene() {
    return column.getTreeTableView().getScene();
  }

  /////////////////////////////////////
  // Useful methods for derived types

  protected abstract ContextMenu buildColumnContextMenu(
      DepanFxDialogRunner depanFxDialogRunner);

  protected abstract TreeTableColumn<DepanFxNodeListMember, ?> buildColumn();

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

  private double getWidthPx() {
    return DepanFxSceneControls.layoutWidthMs(getWidthMs());
  }

  private double getWidthMs() {
    return getColumnData().getWidthMs();
  }
}
