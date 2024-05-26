package com.pnambic.depanfx.scene;

import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class DepanFxTreeColumnBinder<S> {

  private final TreeTableView<S> tableView;

  private int next = 0;

  public DepanFxTreeColumnBinder(TreeTableView<S> tableView) {
    this.tableView = tableView;
  }

  @SuppressWarnings("unchecked")
  public <T> TreeTableColumn<S, T> next() {
    TreeTableColumn<S, T> result =
        (TreeTableColumn<S, T>) tableView.getColumns().get(next);
    next ++;
    return result;
  }
}
