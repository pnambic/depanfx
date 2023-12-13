package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.scene.DepanFxSceneControls;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public abstract class DepanFxAbstractColumn
    implements DepanFxNodeListColumn {

  protected final DepanFxNodeListViewer listViewer;

  public DepanFxAbstractColumn(DepanFxNodeListViewer listViewer) {
    super();
    this.listViewer = listViewer;
  }

  @Override
  public TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> prepareColumn() {
    TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> result =
        new TreeTableColumn<>(getColumnLabel());
    result.setPrefWidth(DepanFxSceneControls.layoutWidthMs(getWidthMs()));

    result.setCellFactory(new ColumnCellFactory());
    result.setCellValueFactory(new ColumnValueFactory());
    return result;
  }

  protected abstract double getWidthMs();

  /////////////////////////////////////
  // Internal Types

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
