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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class DepanFxContextMenuBuilder {

  private final ContextMenu result;

  private final DepanFxMenuItemFactory itemFactory;

  public DepanFxContextMenuBuilder(ContextMenu result) {
    this.result = result;
    this.itemFactory = new DepanFxMenuItemFactory(result.getItems());
  }

  public DepanFxContextMenuBuilder() {
    this(new ContextMenu());
  }

  public MenuItem appendActionItem(
      String label, EventHandler<ActionEvent> handler) {
    return itemFactory.appendActionItem(label, handler);
  }

  public SeparatorMenuItem appendSeparator() {
    return itemFactory.appendSeparator();
  }

  public void appendConditionalSeparator() {
    itemFactory.appendConditionalSeparator();
  }

  public void appendSubMenu(Menu subMenu) {
    itemFactory.appendSubMenu(subMenu);
  }

  public ContextMenu build() {
    return result;
  }
}
