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
package com.pnambic.depanfx.nodelist.gui.columns.infos;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxBaseColumnCell;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;

public class DepanFxDisplayInfoColumnCell extends DepanFxBaseColumnCell {

  public DepanFxDisplayInfoColumnCell(DepanFxNodeListColumn nodeListColumn) {
    super(nodeListColumn);
  }

  @Override
  protected void stylizeCell(DepanFxNodeListMember member) {
    if (member instanceof DepanFxNodeListGraphNode node) {
      setText(getColumn().toString(node));
      return;
    }
    setText("");
  }
}
