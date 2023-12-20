package com.pnambic.depanfx.scene;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class DepanFxContextMenuBuilder {

  private final ContextMenu result;

  private final ObservableList<MenuItem> contextList;

  public DepanFxContextMenuBuilder() {
    this.result = new ContextMenu();
    this.contextList = result.getItems();
  }

  public static MenuItem createActionItem(
      String label, EventHandler<ActionEvent> handler) {
    MenuItem result = new MenuItem(label);
    result.setOnAction(handler);
    return result;
  }

  public MenuItem appendActionItem(
      String label,
      EventHandler<ActionEvent> handler) {
    MenuItem result = createActionItem(label, handler);
    contextList.add(result);
    return result;
  }

  public SeparatorMenuItem appendSeparator() {
    SeparatorMenuItem result = new SeparatorMenuItem();
    contextList.add(result);
    return result;
  }

  public ContextMenu build() {
    return result;
  }

  public void appendSubMenu(Menu subMenu) {
    contextList.add(subMenu);
  }
}
