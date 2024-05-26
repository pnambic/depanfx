package com.pnambic.depanfx.scene;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class DepanFxTableColumnBinder<S> {

  private final TableView<S> tableView;

  private int next = 0;

  public DepanFxTableColumnBinder(TableView<S> tableView) {
    this.tableView = tableView;
  }

  @SuppressWarnings("unchecked")
  public <T> TableColumn<S, T> next() {
    TableColumn<S, T> result =
        (TableColumn<S, T>) tableView.getColumns().get(next);
    next ++;
    return result;
  }

  public <T> TableColumn<S, T> bind(String propName) {
    TableColumn<S, T> result = next();
    result.setCellValueFactory(new PropertyValueFactory<>(propName));
    return result;
  }

  @SuppressWarnings("unchecked")
  public <T> TableColumn<S, T> bind(String propName, Class<?> type) {
    TableColumn<S, T> result = bind(propName);

    T[] values = (T[]) type.getEnumConstants();
    result.setCellFactory(
        ComboBoxTableCell.forTableColumn(values));
    return result;
  }
}
