/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.scene;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * Factory for creating and managing menu items in a context menu.
 *
 * The caller is expected to hold onto and use the container
 * for the menu items, regardless of type ({@code Menu},
 * {@code ContextMenu}, etc.).
 */
public class DepanFxMenuItemFactory {

  private final ObservableList<MenuItem> menuItems;

  public DepanFxMenuItemFactory(ObservableList<MenuItem> menuItems) {
    this.menuItems = menuItems;
  }

  public DepanFxMenuItemFactory(Menu menu) {
    this(menu.getItems());
  }

  public DepanFxMenuItemFactory(ContextMenu menu) {
    this(menu.getItems());
  }

  public static MenuItem createActionItem(
      String label, EventHandler<ActionEvent> handler) {
    MenuItem result = new MenuItem(label);
    result.setOnAction(handler);
    return result;
  }

  public MenuItem appendActionItem(
      String label, EventHandler<ActionEvent> handler) {
    MenuItem result = createActionItem(label, handler);
    menuItems.add(result);
    return result;
  }

  public SeparatorMenuItem appendSeparator() {
    SeparatorMenuItem result = new SeparatorMenuItem();
    menuItems.add(result);
    return result;
  }

  public void appendConditionalSeparator() {
    if (!menuItems.isEmpty()) {
      appendSeparator();
    }
  }

  public void appendSubMenu(Menu subMenu) {
    menuItems.add(subMenu);
  }

  public void appendMenuItem(MenuItem item) {
    menuItems.add(item);
  }
}
