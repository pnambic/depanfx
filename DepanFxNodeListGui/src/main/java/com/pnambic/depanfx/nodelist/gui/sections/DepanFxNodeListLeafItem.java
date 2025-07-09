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
package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListItem;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;


/**
 * A node list item representing a node without children.
 */
public abstract class DepanFxNodeListLeafItem extends DepanFxNodeListItem {

  public DepanFxNodeListLeafItem(DepanFxNodeListMember member) {
    super(member);
  }

  @Override
  public boolean isLeaf() {
    return true;
  }

  @Override
  public void fillNodeContextMenu(
      ContextMenu contextMenu,
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      GraphNode node) {

    DepanFxContextMenuBuilder builder =
        new DepanFxContextMenuBuilder(contextMenu);
    appendCopyActionItems(builder);
  }
}
